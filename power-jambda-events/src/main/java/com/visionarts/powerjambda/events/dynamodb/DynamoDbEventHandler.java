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
import java.util.Collection;
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
import com.visionarts.powerjambda.events.EventConstants.DynamoDbEventName;
import com.visionarts.powerjambda.events.model.AttributeValue;
import com.visionarts.powerjambda.events.model.DynamoDbEvent;
import com.visionarts.powerjambda.events.model.DynamoDbEvent.DynamoDbStreamRecord;
import com.visionarts.powerjambda.events.model.Record.StreamRecord;
import com.visionarts.powerjambda.utils.Utils;


/**
 * The class has the event handler for DynamoDB Streams event.
 *
 */
public class DynamoDbEventHandler extends AbstractEventHandler<DynamoDbEvent, DynamoDbEventResult, List<AwsEventRequest>> {

    private static final int DEFAULT_PARALLEL_SIZE = 1;
    private static final String EMPTY_JSON_CONTENT = "{}";
    private static final AttributeValue DEFAULT_ATTRIBUTES = new AttributeValue(EMPTY_JSON_CONTENT);

    private final Set<DynamoDbEventName> availableEventNames;
    private final int parallelStreamSize;

    private enum Result {
        SUCCEEDED,
        FAILED,

        ;
    }

    private static class DynamoDbStreamRecordResult {
        public final Result result;
        public final AwsEventRequest request;

        DynamoDbStreamRecordResult(Result result, AwsEventRequest request) {
            this.result = result;
            this.request = request;
        }
    }

    /**
     * Constructs new DynamoDbEventHandler instance with default parameters.<br>
     * <br>
     * Note:
     * This event handler's trigger type is {@link DynamoDbEventName#INSERT} and
     * parallelly execute when record size in dynamodb event is greater than {@code 1}.
     */
    public DynamoDbEventHandler(ApplicationContext applicationContext) {
        this(applicationContext, DEFAULT_PARALLEL_SIZE, DynamoDbEventName.INSERT);
    }

    /**
     * Constructs new DynamoDbEventHandler instance with given parameters.<br>
     * <br>
     *
     * @param parallelStreamSize positive stream size to decide parallel execution for actions,
     *                           specify less than or equal this value to execute sequentially
     * @param availableEventNames trigger types of data modification to invoke event action
     */
    public DynamoDbEventHandler(ApplicationContext applicationContext,
                                int parallelStreamSize, DynamoDbEventName... availableEventNames) {
        super(applicationContext);
        this.parallelStreamSize = parallelStreamSize;
        this.availableEventNames = Collections.unmodifiableSet(
                Arrays.stream(availableEventNames).collect(Collectors.toSet()));
    }

    @Override
    public DynamoDbEventResult handleRequest(DynamoDbEvent input, Context context) {
        logger.info("RecordSize : {}", () -> input.getRecords().size());
        return super.handleRequest(input, context);
    }

    @Override
    protected DynamoDbEventResult handleEvent(DynamoDbEvent event, Context context) {
        List<AwsEventRequest> validRequests = readEvent(event);
        Map<Result, List<AwsEventRequest>> reqResults = streamIfAvailableParallel(validRequests)
            .map(req -> handleRouterRequest(req, context))
            .collect(Collectors.groupingByConcurrent(r -> r.result,
                    Collectors.mapping(r -> r.request, Collectors.toList())));

        DynamoDbEventResult result = new DynamoDbEventResult();
        result.setSuccessItems(reqResults.getOrDefault(Result.SUCCEEDED, Collections.emptyList()));
        result.setFailureItems(reqResults.getOrDefault(Result.FAILED, Collections.emptyList()));
        Stream.generate(() -> "skipDummy")
            .limit(event.getRecords().size() - validRequests.size())
            .forEach(e -> result.addSkippedItem(null));
        return result;
    }

    @Override
    public List<AwsEventRequest> readEvent(DynamoDbEvent event) {
        return event.getRecords().stream()
                .filter(r -> availableEventNames.contains(DynamoDbEventName.valueOf(r.getEventName())))
                .map(r -> readDynamoDbStreamRecord(r))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <E> Stream<E> streamIfAvailableParallel(List<E> list) {
        return Optional.of(list)
                .filter(l -> l.size() > parallelStreamSize)
                .map(Collection::parallelStream)
                .orElseGet(list::stream);
    }

    private DynamoDbStreamRecordResult handleRouterRequest(AwsEventRequest request, Context context) {
        AwsEventResponse res = actionRouterHandle(request, context);
        if (res.isSuccessful()) {
            return new DynamoDbStreamRecordResult(Result.SUCCEEDED, request);
        } else {
            return new DynamoDbStreamRecordResult(Result.FAILED, request);
        }
    }

    protected AwsEventRequest readDynamoDbStreamRecord(DynamoDbStreamRecord dynamoDbStreamRecord) {
        String eventId = dynamoDbStreamRecord.getEventId();
        StreamRecord record = dynamoDbStreamRecord.getDynamodb();
        Map<String, AttributeValue> newImage = record.getNewImage();
        if (!containsRequiredKeys(newImage)) {
            logger.error("Skip record : eventID = {} missing required key", eventId);
            return null;
        }

        AttributeValue action = newImage.get(EventConstants.DYNAMODB_ATTR_ACTION);
        AttributeValue body = newImage.get(EventConstants.DYNAMODB_ATTR_REQUEST_BODY);
        Map<String, String> eventAttrs;
        try {
            eventAttrs = getEventAttributes(newImage);
        } catch (IOException e) {
            logger.error("Skip record : eventID = {} failed to deserialize JSON content {} in {}, msg = {}",
                    eventId,
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

    private boolean containsRequiredKeys(Map<String, AttributeValue> record) {
        Optional<Map<String, AttributeValue>> result = Optional.of(record)
            .filter(r -> r.containsKey(EventConstants.DYNAMODB_ATTR_ACTION))
            .filter(r -> r.containsKey(EventConstants.DYNAMODB_ATTR_REQUEST_BODY));
        return result.isPresent();
    }

    protected Map<String, String> getEventAttributes(Map<String, AttributeValue> image) throws IOException {
        String attrsString = image.getOrDefault(EventConstants.DYNAMODB_ATTR_EVENT_ATTRIBUTES, DEFAULT_ATTRIBUTES)
                .getS();
        return Utils.getObjectMapper()
                .readValue(attrsString, new TypeReference<Map<String, String>>() {
                    // nothing to do
                    }
                );
    }

}
