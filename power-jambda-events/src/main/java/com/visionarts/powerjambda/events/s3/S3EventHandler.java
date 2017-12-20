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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.AbstractEventHandler;
import com.visionarts.powerjambda.events.AwsEventRequest;
import com.visionarts.powerjambda.events.AwsEventResponse;


/**
 * The class has the event handler for S3 event.
 *
 */
public class S3EventHandler extends AbstractEventHandler<S3EventNotification, S3EventResult, AwsEventRequest> {

    private final Class<?> s3Action;

    public S3EventHandler(ApplicationContext applicationContext, Class<?> s3Action) {
        super(applicationContext);
        this.s3Action = s3Action;
    }

    @Override
    public AwsEventRequest readEvent(S3EventNotification event) {
        return new AwsEventRequest()
                .action(s3Action.getName())
                .body(event.toJson());
    }

    @Override
    protected S3EventResult handleEvent(S3EventNotification event, Context context) {
        AwsEventRequest request = readEvent(event);
        S3EventResult result = new S3EventResult();
        AwsEventResponse res = actionRouterHandle(request, context);
        if (res.isSuccessful()) {
            result.addSuccessItem(request);
        } else {
            logger.error("Failed processing S3Event", res.getCause());
            result.addFailureItem(request);
        }
        return result;
    }

}
