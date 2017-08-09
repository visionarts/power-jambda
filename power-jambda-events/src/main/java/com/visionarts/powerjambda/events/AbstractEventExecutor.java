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
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.visionarts.powerjambda.ApplicationContext;

public abstract class AbstractEventExecutor<EventType, EventResultType extends Result<?>> implements EventResolver<EventType, EventResultType> {

    protected final Logger logger = LogManager.getLogger(this.getClass());

    private ApplicationContext applicationContext;
    private boolean throwOnFailure;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public boolean isThrowOnFailure() {
        return throwOnFailure;
    }

    public void setThrowOnFailure(boolean throwOnFailure) {
        this.throwOnFailure = throwOnFailure;
    }

    public AbstractEventExecutor<EventType, EventResultType> throwOnFailure(boolean throwOnFailure) {
        setThrowOnFailure(throwOnFailure);
        return this;
    }


    public final Optional<EventResultType> apply(final byte[] input, Context context) {
        Optional<EventResultType> result = handleEvent(() -> resolve(input), context);
        result.ifPresent(r -> r.setThrowOnFailure(isThrowOnFailure()));
        return result;
    }

    private Optional<EventResultType> handleEvent(Supplier<Optional<EventType>> eventSupplier, Context context) {
        return eventSupplier.get().map(e -> {
            RequestHandler<EventType, EventResultType> handler = getEventHandler().get();
            return handler.handleRequest(e, context);
        });
    }

}
