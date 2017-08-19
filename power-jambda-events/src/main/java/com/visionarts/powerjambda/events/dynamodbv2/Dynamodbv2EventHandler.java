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

package com.visionarts.powerjambda.events.dynamodbv2;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.AwsEventRequest;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.EventConstants.DynamoDBEventName;
import com.visionarts.powerjambda.events.dynamodb.DynamodbEventHandler;
import com.visionarts.powerjambda.events.model.AttributeValueEx;
import com.visionarts.powerjambda.events.model.DynamodbEventEx.DynamodbStreamRecord;
import com.visionarts.powerjambda.events.model.EventActionRequest;
import com.visionarts.powerjambda.events.model.RecordEx.StreamRecordEx;
import com.visionarts.powerjambda.utils.Utils;


/**
 * The class has the event handler for DynamoDB Streams event, <br>
 * and allows you to pass all or part of attribute values in a DynamoDB Streams to AwsEventRequest.
 *
 */
public class Dynamodbv2EventHandler extends DynamodbEventHandler {

    private Function<DynamodbStreamRecord, ?> requestBodyMapper;

    /**
     * Constructs new Dynamodbv2EventHandler instance with given parameters.<br>
     * <br>
     *
     * @param applicationContext The application context object
     * @param parallelStreamSize A positive stream size to decide parallel execution for actions,
     *                           specify less than or equal this value to execute sequentially
     * @param requestBodyMapper The mapper to return the corresponding a body object in {@link EventActionRequest}
     *                          using attribute values in a record of DynamoDB Streams
     * @param availableEventNames Trigger types of data modification to invoke event action
     */
    public Dynamodbv2EventHandler(ApplicationContext applicationContext,
                                  int parallelStreamSize,
                                  Function<DynamodbStreamRecord, ?> requestBodyMapper,
                                  DynamoDBEventName... availableEventNames) {
        super(applicationContext, parallelStreamSize, availableEventNames);
        this.requestBodyMapper = Objects.requireNonNull(requestBodyMapper);
    }

    @Override
    protected AwsEventRequest readDynamodbStreamRecord(DynamodbStreamRecord dynamodbStreamRecord) {
        String eventID = dynamodbStreamRecord.getEventID();
        StreamRecordEx record = dynamodbStreamRecord.getDynamodb();
        Map<String, AttributeValueEx> newImage = record.getNewImage();
        if (!containsRequiredKeys(newImage)) {
            logger.error("Skip record : eventID = {} missing required key", eventID);
            return null;
        }

        AttributeValueEx action = newImage.get(EventConstants.DYNAMODB_ATTR_ACTION);
        String body;
        try {
            body = Utils.getObjectMapper().writeValueAsString(requestBodyMapper.apply(dynamodbStreamRecord));
        } catch (JsonProcessingException e) {
            logger.error("Skip record : eventID = {} failed to serialize event request body, msg = {}",
                    eventID, e.getMessage());
            return null;
        }
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
                    .body(body)
                    .attributes(eventAttrs);
    }

    private boolean containsRequiredKeys(Map<String, AttributeValueEx> record) {
        Optional<Map<String, AttributeValueEx>> result = Optional.of(record)
            .filter(r -> r.containsKey(EventConstants.DYNAMODB_ATTR_ACTION));
        return result.isPresent();
    }

}
