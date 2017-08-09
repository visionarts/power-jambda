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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.visionarts.powerjambda.AbstractRouter;
import com.visionarts.powerjambda.ApplicationContext;

public abstract class AbstractEventHandler<EventType, EventResultType, RouterRequest>
    implements RequestHandler<EventType, EventResultType>, EventRequestReader<EventType, RouterRequest> {

    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected final ApplicationContext applicationContext;

    public AbstractEventHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * The entry method for handling events.<br>
     * <br>
     *
     */
    @Override
    public EventResultType handleRequest(EventType event, Context context) {
        return handleEvent(event, context);
    }

    protected abstract EventResultType handleEvent(EventType event, Context context);

    /**
     * Convenient method forms pass through to <br>
     * {@link AbstractRouter#apply(Object, Context)}
     *
     * @param request
     * @param context
     * @return
     */
    protected final AwsEventResponse actionRouterHandle(AwsEventRequest request, Context context) {
        return new EventActionRouter(applicationContext)
                    .apply(request, context);
    }

}
