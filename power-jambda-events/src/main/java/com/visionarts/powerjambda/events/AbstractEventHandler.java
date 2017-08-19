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

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.visionarts.powerjambda.AbstractRouter;
import com.visionarts.powerjambda.ApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractEventHandler<EventT, EventResultT, RouterRequestT>
    implements RequestHandler<EventT, EventResultT>, EventRequestReader<EventT, RouterRequestT> {

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
    public EventResultT handleRequest(EventT event, Context context) {
        return handleEvent(event, context);
    }

    protected abstract EventResultT handleEvent(EventT event, Context context);

    /**
     * Convenient method forms pass through to <br>
     * {@link AbstractRouter#apply(Object, Context)}
     *
     * @param request The incoming request
     * @param context The Lambda Context object passed by the AWS Lambda environment
     * @return The response returned by the action class
     */
    protected final AwsEventResponse actionRouterHandle(AwsEventRequest request, Context context) {
        return new EventActionRouter(applicationContext)
                    .apply(request, context);
    }

}
