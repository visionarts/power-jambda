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
package com.visionarts.powerjambda.events.s3;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.logging.log4j.ThreadContext;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.visionarts.powerjambda.events.AbstractEventExecutor;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.action.AbstractEventAction;
import com.visionarts.powerjambda.events.internal.EventDeserializeUtils;
import com.visionarts.powerjambda.utils.FunctionalUtils;

public class S3EventExecutor extends AbstractEventExecutor<S3EventNotification, S3EventResult> {

    public static final Predicate<S3EventNotification> S3_EVENT_CONDITION = e ->
    FunctionalUtils.isNotEmpty(e.getRecords()) &&
        EventConstants.EVENT_SOURCE_S3.equals(e.getRecords().get(0).getEventSource());

    private final Class<?> s3Action;

    public S3EventExecutor(Class<? extends AbstractEventAction<?>> s3Action) {
        this.s3Action = Objects.requireNonNull(s3Action, "s3Action must not be null");
    }

    @Override
    public Optional<S3EventNotification> resolve(byte[] input) {
        Optional<S3EventNotification> event;
        try {
            event = EventDeserializeUtils.resolveAWSEvent(
                        input, S3EventNotification.class, S3_EVENT_CONDITION);
            event.ifPresent(e -> {
                ThreadContext.put(EventConstants.LOG_THREAD_CONTEXT_EVENT_KEY,
                        EventConstants.EVENT_SOURCE_S3);
                logger.info("Deserialize current input to S3EventNotification instance successfully");
            });
        } catch (IOException e) {
            // fall through
            event = Optional.empty();
            logger.error("Can not deserialize S3EventNotification", e);
        }
        return event;
    }

    @Override
    public Supplier<RequestHandler<S3EventNotification, S3EventResult>> getEventHandler() {
        return () -> new S3EventHandler(getApplicationContext(), s3Action);
    }

}
