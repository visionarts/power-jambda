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
import java.util.Optional;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.visionarts.powerjambda.events.EventConstants;
import com.visionarts.powerjambda.events.action.AbstractEventAction;
import com.visionarts.powerjambda.events.internal.EventDeserializeUtils;
import com.visionarts.powerjambda.events.model.SnsEvent;
import com.visionarts.powerjambda.events.sns.SnsEventExecutor;
import org.apache.logging.log4j.ThreadContext;

public class S3EventViaSnsExecutor extends S3EventExecutor {

    public S3EventViaSnsExecutor(Class<? extends AbstractEventAction<?>> s3Action) {
        super(s3Action);
    }

    @Override
    public Optional<S3EventNotification> resolve(final byte[] input) {
        Optional<SnsEvent> snsEvent = resolveSnsEvent(input);
        return snsEvent.flatMap(sns -> {
            try {
                String message = sns.getRecords().get(0).getSns().getMessage();
                Optional<S3EventNotification> event = EventDeserializeUtils.resolveAwsEvent(
                            message.getBytes(), S3EventNotification.class, S3_EVENT_CONDITION);
                event.ifPresent(e -> {
                    ThreadContext.put(EventConstants.LOG_THREAD_CONTEXT_EVENT_KEY,
                            EventConstants.EVENT_SOURCE_S3_VIA_SNS);
                    logger.info("Deserialize current input to S3EventNotification via Sns instance successfully");
                });
                return event;
            } catch (IOException e) {
                // fall through
                logger.error("Can not deserialize S3EventNotification via Sns", e);
                return Optional.empty();
            }
        });
    }

    private Optional<SnsEvent> resolveSnsEvent(final byte[] input) {
        Optional<SnsEvent> event;
        try {
            event = EventDeserializeUtils.resolveAwsEvent(
                        input, SnsEvent.class, SnsEventExecutor.SNS_EVENT_CONDITION);
        } catch (IOException e) {
            // fall through
            event = Optional.empty();
            logger.error("Can not deserialize SnsEvent", e);
        }
        return event;
    }

}
