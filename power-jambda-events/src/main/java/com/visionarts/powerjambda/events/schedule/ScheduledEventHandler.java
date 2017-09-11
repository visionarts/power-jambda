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

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.AbstractEventHandler;
import com.visionarts.powerjambda.events.AwsEventRequest;
import com.visionarts.powerjambda.events.AwsEventResponse;
import com.visionarts.powerjambda.events.model.ScheduledEvent;


/**
 * The class has the event handler for Scheduled Event.
 *
 */
public class ScheduledEventHandler extends AbstractEventHandler<ScheduledEvent, ScheduledEventResult, AwsEventRequest> {

    public ScheduledEventHandler(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected ScheduledEventResult handleEvent(ScheduledEvent event, Context context) {
        AwsEventRequest request = readEvent(event);
        ScheduledEventResult result = new ScheduledEventResult();
        AwsEventResponse res = actionRouterHandle(request, context);
        if (res.isSuccessful()) {
            result.addSuccessItem(request);
        } else {
            result.addFailureItem(request);
        }
        return result;
    }

    @Override
    public AwsEventRequest readEvent(ScheduledEvent event) {
        return new AwsEventRequest()
                    .action(event.getDetail().get("action").asText())
                    .body(event.getDetail().get("body").asText());
    }

}
