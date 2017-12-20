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
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.visionarts.powerjambda.events.AbstractEventExecutor;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.internal.EventDeserializeUtils;
import com.visionarts.powerjambda.events.model.DynamoDbEvent;
import com.visionarts.powerjambda.utils.FunctionalUtils;
import org.apache.logging.log4j.ThreadContext;

public class DynamoDbEventExecutor extends AbstractEventExecutor<DynamoDbEvent, DynamoDbEventResult> {

    private static final Predicate<DynamoDbEvent> DYNAMODB_EVENT_CONDITION = e ->
        FunctionalUtils.isNotEmpty(e.getRecords()) &&
            EventConstants.EVENT_SOURCE_DYNAMODB.equals(e.getRecords().get(0).getEventSource());

    private final Supplier<RequestHandler<DynamoDbEvent, DynamoDbEventResult>> supplier;

    public DynamoDbEventExecutor() {
        supplier = () -> new DynamoDbEventHandler(getApplicationContext());
    }

    public DynamoDbEventExecutor(int parallelStreamSize, EventConstants.DynamoDbEventName... availableEventTypes) {
        supplier = () ->
            new DynamoDbEventHandler(getApplicationContext(), parallelStreamSize, availableEventTypes);
    }

    @Override
    public Optional<DynamoDbEvent> resolve(final byte[] input) {
        Optional<DynamoDbEvent> event;
        try {
            event = EventDeserializeUtils.resolveAwsEvent(
                        input, DynamoDbEvent.class, DYNAMODB_EVENT_CONDITION);
            event.ifPresent(e -> {
                ThreadContext.put(EventConstants.LOG_THREAD_CONTEXT_EVENT_KEY,
                        EventConstants.EVENT_SOURCE_DYNAMODB);
                logger.info("Successfully deserialized current input to DynamoDbEvent");
            });
        } catch (IOException e) {
            // fall through
            event = Optional.empty();
            logger.error("Can not deserialize DynamoDbEvent", e);
        }
        return event;
    }

    @Override
    public Supplier<RequestHandler<DynamoDbEvent, DynamoDbEventResult>> getEventHandler() {
        return supplier;
    }
}
