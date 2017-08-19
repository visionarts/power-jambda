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

package com.visionarts.powerjambda.events;

import java.util.Optional;

import com.visionarts.powerjambda.AbstractRouter;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.GlobalExceptionHandler;


/**
 * The class has the main event handler for the Lambda event.
 *
 */
public class EventActionRouter extends AbstractRouter<AwsEventRequest, AwsEventResponse> {

    public EventActionRouter(ApplicationContext applicationContext) {
        this(applicationContext, new EventGlobalExceptionHandler());
    }

    public EventActionRouter(ApplicationContext applicationContext,
                             GlobalExceptionHandler<AwsEventResponse> exceptionHandler) {
        super(applicationContext, exceptionHandler);
    }

    @Override
    protected Optional<Class<?>> findAction(String packagePath, AwsEventRequest request) {
        logger.debug("action={}", () -> request.getAction());
        try {
            return Optional.ofNullable(Class.forName(request.getAction()));
        } catch (ClassNotFoundException e) {
            logger.error("Illegal a FQCN of the action : {}", () -> e.getMessage());
            return Optional.empty();
        }
    }

}
