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
package com.visionarts.powerjambda.events.actions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.visionarts.powerjambda.events.action.AbstractEventAction;
import com.visionarts.powerjambda.events.model.EventActionRequest;

/**
 * . <br>
 * <br>
 */
public class S3EventAction extends AbstractEventAction<S3EventNotification> {

    @Override
    public Class<S3EventNotification> actionBodyType() {
        return S3EventNotification.class;
    }

    @Override
    protected void handleEvent(EventActionRequest<S3EventNotification> request, Context context) throws Exception {
        logger.info("{}", () -> request.getBody().toJson());
    }
}
