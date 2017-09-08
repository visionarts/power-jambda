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
import java.io.UncheckedIOException;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.JsonNode;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.AbstractEventHandler;
import com.visionarts.powerjambda.events.AwsEventRequest;
import com.visionarts.powerjambda.events.AwsEventResponse;
import com.visionarts.powerjambda.events.model.SnsEvent;
import com.visionarts.powerjambda.events.model.SnsEvent.Sns;
import com.visionarts.powerjambda.utils.Utils;


/**
 * The class has the event handler for Sns event.
 *
 */
public class SnsEventHandler extends AbstractEventHandler<SnsEvent, SnsEventResult, AwsEventRequest> {

    private static class SnsEventMessage {
        public String action;
        public JsonNode body;
        public Map<String, String> eventAttrs;
    }

    public SnsEventHandler(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public AwsEventRequest readEvent(SnsEvent event) {
        Sns sns = event.getRecords().get(0).getSns();
        try {
            return buildRequest(sns);
        } catch (IOException e) {
            throw logger.throwing(new UncheckedIOException(e));
        }
    }

    protected AwsEventRequest buildRequest(Sns sns) throws IOException {
        String subject = sns.getSubject();
        String message = sns.getMessage();

        logger.info("Sns Notification : {}", subject);
        SnsEventMessage msg = Utils.getObjectMapper().readValue(message, SnsEventMessage.class);
        return new AwsEventRequest()
                    .action(msg.action)
                    .body(msg.body.toString())
                    .attributes(msg.eventAttrs);
    }

    @Override
    protected SnsEventResult handleEvent(SnsEvent event, Context context) {
        AwsEventRequest request = readEvent(event);
        SnsEventResult result = new SnsEventResult();
        AwsEventResponse res = actionRouterHandle(request, context);
        if (res.isSuccessful()) {
            result.addSuccessItem(request);
        } else {
            logger.error("failed processing SNSEvent", res.getCause());
            result.addFailureItem(request);
        }
        return result;
    }

}
