/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.visionarts.powerjambda.events.dynamodb;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.type.TypeReference;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.AbstractEventHandler;
import com.visionarts.powerjambda.events.AwsEventRequest;
import com.visionarts.powerjambda.events.AwsEventResponse;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.EventConstants.DynamoDBEventName;
import com.visionarts.powerjambda.events.model.AttributeValueEx;
import com.visionarts.powerjambda.events.model.DynamodbEventEx;
import com.visionarts.powerjambda.events.model.DynamodbEventEx.DynamodbStreamRecord;
import com.visionarts.powerjambda.events.model.RecordEx.StreamRecordEx;
import com.visionarts.powerjambda.utils.Utils;


/**
 * The class has the event handler for DynamoDB Streams event.
 *
 */
public class DynamodbEventHandler extends AbstractEventHandler<DynamodbEventEx, DynamodbEventResult, List<AwsEventRequest>> {

    private static final int DEFAULT_PARALLEL_SIZE = 1;
    private static final String EMPTY_JSON_CONTENT = "{}";
    private static final AttributeValueEx DEFAULT_ATTRIBUTES = new AttributeValueEx(EMPTY_JSON_CONTENT);

    private final Set<DynamoDBEventName> availableEventNames;
    private final int parallelStreamSize;

    private enum Result {
        SUCCEEDED,
        FAILED,

        ;
    }

    private static class DynamodbStreamRecordResult {
        public final Result result;
        public final AwsEventRequest request;

        DynamodbStreamRecordResult(Result result, AwsEventRequest request) {
            this.result = result;
            this.request = request;
        }
    }

    /**
     * Constructs new DynamodbEventHandler instance with default parameters.<br>
     * <br>
     * Note:
     * This event handler's trigger type is {@link DynamoDBEventName#INSERT} and
     * parallelly execute when record size in dynamodb event is greater than {@code 1}.
     */
    public DynamodbEventHandler(ApplicationContext applicationContext) {
        this(applicationContext, DEFAULT_PARALLEL_SIZE, DynamoDBEventName.INSERT);
    }

    /**
     * Constructs new DynamodbEventHandler instance with given parameters.<br>
     * <br>
     *
     * @param parallelStreamSize positive stream size to decide parallel execution for actions,
     *                           specify less than or equal this value to execute sequentially
     * @param availableEventNames trigger types of data modification to invoke event action
     */
    public DynamodbEventHandler(ApplicationContext applicationContext,
                                int parallelStreamSize, DynamoDBEventName... availableEventNames) {
        super(applicationContext);
        this.parallelStreamSize = parallelStreamSize;
        this.availableEventNames = Collections.unmodifiableSet(
                Arrays.stream(availableEventNames).collect(Collectors.toSet()));
    }

    @Override
    public DynamodbEventResult handleRequest(DynamodbEventEx input, Context context) {
        logger.info("RecordSize : {}", () -> input.getRecords().size());
        return super.handleRequest(input, context);
    }

    @Override
    protected DynamodbEventResult handleEvent(DynamodbEventEx event, Context context) {
        List<AwsEventRequest> validRequests = readEvent(event);
        Map<Result, List<AwsEventRequest>> reqResults = streamIfAvailableParallel(validRequests)
            .map(req -> handleRouterRequest(req, context))
            .collect(Collectors.groupingByConcurrent(r -> r.result,
                    Collectors.mapping(r -> r.request, Collectors.toList())));

        DynamodbEventResult result = new DynamodbEventResult();
        result.setSuccessItems(reqResults.getOrDefault(Result.SUCCEEDED, Collections.emptyList()));
        result.setFailureItems(reqResults.getOrDefault(Result.FAILED, Collections.emptyList()));
        Stream.generate(() -> "skipDummy")
            .limit(event.getRecords().size() - validRequests.size())
            .forEach(e -> result.addSkippedItem(null));
        return result;
    }

    @Override
    public List<AwsEventRequest> readEvent(DynamodbEventEx event) {
        return event.getRecords().stream()
                .filter(r -> availableEventNames.contains(DynamoDBEventName.valueOf(r.getEventName())))
                .map(r -> readDynamodbStreamRecord(r))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <E> Stream<E> streamIfAvailableParallel(List<E> list) {
        return Optional.of(list)
                .filter(l -> l.size() > parallelStreamSize)
                .map(l -> l.parallelStream())
                .orElseGet(() -> list.stream());
    }

    private DynamodbStreamRecordResult handleRouterRequest(AwsEventRequest request, Context context) {
        AwsEventResponse res = actionRouterHandle(request, context);
        if (res.isSuccessful()) {
            return new DynamodbStreamRecordResult(Result.SUCCEEDED, request);
        } else {
            return new DynamodbStreamRecordResult(Result.FAILED, request);
        }
    }

    protected AwsEventRequest readDynamodbStreamRecord(DynamodbStreamRecord dynamodbStreamRecord) {
        String eventID = dynamodbStreamRecord.getEventID();
        StreamRecordEx record = dynamodbStreamRecord.getDynamodb();
        Map<String, AttributeValueEx> newImage = record.getNewImage();
        if (!containsRequiredKeys(newImage)) {
            logger.error("Skip record : eventID = {} missing required key", eventID);
            return null;
        }

        AttributeValueEx action = newImage.get(EventConstants.DYNAMODB_ATTR_ACTION);
        AttributeValueEx body = newImage.get(EventConstants.DYNAMODB_ATTR_REQUEST_BODY);
        Map<String, String> eventAttrs;
        try {
            eventAttrs = getEventAttributes(newImage);
        } catch (IOException e) {
            logger.error("Skip record : eventID = {} failed to deserialize JSON content {} in {}, msg = {}",
                    eventID,
                    newImage.get(EventConstants.DYNAMODB_ATTR_EVENT_ATTRIBUTES).getS(),
                    EventConstants.DYNAMODB_ATTR_EVENT_ATTRIBUTES,
                    e.getMessage());
            return null;
        }
        return new AwsEventRequest()
                    .action(action.getS())
                    .body(body.getS())
                    .attributes(eventAttrs);
    }

    private boolean containsRequiredKeys(Map<String, AttributeValueEx> record) {
        Optional<Map<String, AttributeValueEx>> result = Optional.of(record)
            .filter(r -> r.containsKey(EventConstants.DYNAMODB_ATTR_ACTION))
            .filter(r -> r.containsKey(EventConstants.DYNAMODB_ATTR_REQUEST_BODY));
        return result.isPresent();
    }

    protected Map<String, String> getEventAttributes(Map<String, AttributeValueEx> image) throws IOException {
        String attrsString = image.getOrDefault(EventConstants.DYNAMODB_ATTR_EVENT_ATTRIBUTES, DEFAULT_ATTRIBUTES)
                .getS();
        return Utils.getObjectMapper()
                .readValue(attrsString, new TypeReference<Map<String, String>>() {
                    // nothing to do
                    }
                );
    }

}
