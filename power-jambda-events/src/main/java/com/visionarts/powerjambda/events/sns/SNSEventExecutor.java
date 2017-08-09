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

import org.apache.logging.log4j.ThreadContext;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.visionarts.powerjambda.events.AbstractEventExecutor;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.internal.EventDeserializeUtils;
import com.visionarts.powerjambda.events.model.SNSEventEx;
import com.visionarts.powerjambda.utils.FunctionalUtils;

public class SNSEventExecutor extends AbstractEventExecutor<SNSEventEx, SNSEventResult> {

    public static final Predicate<SNSEventEx> SNS_EVENT_CONDITION = e ->
        FunctionalUtils.isNotEmpty(e.getRecords()) &&
            EventConstants.EVENT_SOURCE_SNS.equals(e.getRecords().get(0).getEventSource());

    @Override
    public Optional<SNSEventEx> resolve(final byte[] input) {
        Optional<SNSEventEx> event;
        try {
            event = EventDeserializeUtils.resolveAWSEvent(
                        input, SNSEventEx.class, SNS_EVENT_CONDITION);
            event.ifPresent(e -> {
                ThreadContext.put(EventConstants.LOG_THREAD_CONTEXT_EVENT_KEY,
                        EventConstants.EVENT_SOURCE_SNS);
                logger.info("Deserialize current input to SNSEventEx instance successfully");
            });
        } catch (IOException e) {
            // fall through
            event = Optional.empty();
            logger.error("Can not deserialize SNSEventEx", e);
        }
        return event;
    }

    @Override
    public Supplier<RequestHandler<SNSEventEx, SNSEventResult>> getEventHandler() {
        return () -> new SNSEventHandler(getApplicationContext());
    }
}
