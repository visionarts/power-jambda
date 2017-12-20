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

package com.visionarts.powerjambda.events.schedule;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.visionarts.powerjambda.events.AbstractEventExecutor;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.internal.EventDeserializeUtils;
import com.visionarts.powerjambda.events.model.ScheduledEvent;
import org.apache.logging.log4j.ThreadContext;

public class ScheduledEventExecutor extends AbstractEventExecutor<ScheduledEvent, ScheduledEventResult> {

    private static final Predicate<ScheduledEvent> SCHEDULED_EVENT_CONDITION = e ->
        EventConstants.EVENT_SOURCE_SCHEDULED_EVENTS.equals(e.getSource()) &&
            EventConstants.DETAIL_TYPE_SCHEDULED_EVENT.equals(e.getDetailType());

    @Override
    public Optional<ScheduledEvent> resolve(final byte[] input) {
        Optional<ScheduledEvent> event;
        try {
            event = EventDeserializeUtils.resolveAwsEvent(
                        input, ScheduledEvent.class, SCHEDULED_EVENT_CONDITION);
            event.ifPresent(e -> {
                ThreadContext.put(EventConstants.LOG_THREAD_CONTEXT_EVENT_KEY,
                        EventConstants.EVENT_SOURCE_SCHEDULED_EVENTS);
                logger.info("Successfully deserialized current input to ScheduledEvent");
            });
        } catch (IOException e) {
            // fall through
            event = Optional.empty();
            logger.error("Can not deserialize ScheduledEvent", e);
        }
        return event;
    }

    @Override
    public Supplier<RequestHandler<ScheduledEvent, ScheduledEventResult>> getEventHandler() {
        return () -> new ScheduledEventHandler(getApplicationContext());
    }
}
