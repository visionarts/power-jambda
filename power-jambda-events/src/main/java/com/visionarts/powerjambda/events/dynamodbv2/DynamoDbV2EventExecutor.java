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

import java.util.function.Function;
import java.util.function.Supplier;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.dynamodb.DynamoDbEventExecutor;
import com.visionarts.powerjambda.events.dynamodb.DynamoDbEventResult;
import com.visionarts.powerjambda.events.model.DynamoDbEvent;
import com.visionarts.powerjambda.events.model.DynamoDbEvent.DynamoDbStreamRecord;
import com.visionarts.powerjambda.events.model.EventActionRequest;

/**
 * The class has the event executor for DynamoDB Streams event, <br>
 * and allows you to pass all or part of attribute values in a DynamoDB Streams to AwsEventRequest. <br>
 * <br>
 *
 * <b>Caution:</b>
 * This class must be mutually exclusive with {@link DynamoDbEventExecutor}.
 */
public class DynamoDbV2EventExecutor extends DynamoDbEventExecutor {

    private final Supplier<RequestHandler<DynamoDbEvent, DynamoDbEventResult>> supplier;

    /**
     * Constructs new DynamoDbV2EventExecutor instance with given parameters.<br>
     * <br>
     *
     * @param parallelStreamSize A positive stream size to decide parallel execution for actions,
     *                           specify less than or equal this value to execute sequentially
     * @param requestBodyMapper The mapper to return the corresponding a body object in {@link EventActionRequest}
     *                          using attribute values in a record of DynamoDB Streams
     * @param availableEventNames Trigger types of data modification to invoke event action
     */
    public DynamoDbV2EventExecutor(
            int parallelStreamSize,
            Function<DynamoDbStreamRecord, ?> requestBodyMapper,
            EventConstants.DynamoDbEventName... availableEventNames) {
        supplier = () ->
            new DynamoDbV2EventHandler(getApplicationContext(), parallelStreamSize,
                    requestBodyMapper, availableEventNames);
    }

    @Override
    public Supplier<RequestHandler<DynamoDbEvent, DynamoDbEventResult>> getEventHandler() {
        return supplier;
    }

}
