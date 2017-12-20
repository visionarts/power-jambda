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

package com.visionarts.powerjambda.events.sns;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.visionarts.powerjambda.events.AbstractEventExecutor;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.internal.EventDeserializeUtils;
import com.visionarts.powerjambda.events.model.SnsEvent;
import com.visionarts.powerjambda.utils.FunctionalUtils;
import org.apache.logging.log4j.ThreadContext;

public class SnsEventExecutor extends AbstractEventExecutor<SnsEvent, SnsEventResult> {

    public static final Predicate<SnsEvent> SNS_EVENT_CONDITION = e ->
        FunctionalUtils.isNotEmpty(e.getRecords()) &&
            EventConstants.EVENT_SOURCE_SNS.equals(e.getRecords().get(0).getEventSource());

    @Override
    public Optional<SnsEvent> resolve(final byte[] input) {
        Optional<SnsEvent> event;
        try {
            event = EventDeserializeUtils.resolveAwsEvent(
                        input, SnsEvent.class, SNS_EVENT_CONDITION);
            event.ifPresent(e -> {
                ThreadContext.put(EventConstants.LOG_THREAD_CONTEXT_EVENT_KEY,
                        EventConstants.EVENT_SOURCE_SNS);
                logger.info("Successfully deserialized current input to SnsEvent instance");
            });
        } catch (IOException e) {
            // fall through
            event = Optional.empty();
            logger.error("Can not deserialize SnsEvent", e);
        }
        return event;
    }

    @Override
    public Supplier<RequestHandler<SnsEvent, SnsEventResult>> getEventHandler() {
        return () -> new SnsEventHandler(getApplicationContext());
    }
}
