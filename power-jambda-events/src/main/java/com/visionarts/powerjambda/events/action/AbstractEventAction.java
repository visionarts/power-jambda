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

package com.visionarts.powerjambda.events.action;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.RequestReader;
import com.visionarts.powerjambda.ResponseWriter;
import com.visionarts.powerjambda.actions.LambdaBaseAction;
import com.visionarts.powerjambda.events.AwsEventRequest;
import com.visionarts.powerjambda.events.AwsEventResponse;
import com.visionarts.powerjambda.events.DummyResponseWriter;
import com.visionarts.powerjambda.events.JsonBodyEventActionRequestReader;
import com.visionarts.powerjambda.events.model.EventActionRequest;
import com.visionarts.powerjambda.exceptions.DefaultActionExceptionResolver;
import com.visionarts.powerjambda.exceptions.InternalErrorException;
import com.visionarts.powerjambda.utils.FunctionalUtils;

/**
 * This abstract class implementing the LambdaAction interface.
 * All event action classes must inherit this abstract class.<br>
 * <br>
 * @param <T> The body parameter type in the event action request
 */
public abstract class AbstractEventAction<T>
        extends LambdaBaseAction<AwsEventRequest, AwsEventResponse, EventActionRequest<T>, AwsEventResponse> {

    /**
     * Returns the type of the class modeled by the body type in the action request. <br>
     *
     * For deserializing body in the action request.
     *
     * @return The body type in the action request
     */
    public abstract Class<T> actionBodyType();

    /**
     * Note: Internal Use only<br>
     * <br>
     * Please override {@link #handleEvent(EventActionRequest, Context)} method for your event action.<br>
     *
     */
    @Override
    public final AwsEventResponse handle(EventActionRequest<T> request, Context context) throws Exception {
        handleEvent(request, context);
        return AwsEventResponse.AWS_EVENT_SUCCESSFUL_RESPONSE;
    }

    /**
     * Handles the event action request.<br>
     *
     * <p> This method should be overridden by subclasses.
     *
     * @param request The event request
     * @param context The Lambda context passed by the AWS Lambda environment
     * @throws Exception Thrown if any errors occur
     */
    protected abstract void handleEvent(EventActionRequest<T> request, Context context) throws Exception;

    @Override
    public AwsEventResponse handleException(Throwable exception, Context context) throws InternalErrorException {
        DefaultActionExceptionResolver<AwsEventResponse> resolver =
                new DefaultActionExceptionResolver<>(new DummyResponseWriter<>());
        return resolver.handleException(exception, this, context);
    }

    @Override
    protected final void beforeHandle(EventActionRequest<T> request, Context context) throws Exception {
        FunctionalUtils.toBiConsumer(loggerBeforeHandle(), logger)
                .accept(request, context);
        beforeAction(request, context);
    }

    @Override
    protected final void afterHandle(EventActionRequest<T> request, Context context) throws Exception {
        try {
            afterAction(request, context);
        } finally {
            FunctionalUtils.toBiConsumer(loggerAfterHandle(), logger)
                    .accept(request, context);
        }
    }

    @Override
    protected RequestReader<AwsEventRequest, EventActionRequest<T>> requestReader(AwsEventRequest request, Context context) {
        return new JsonBodyEventActionRequestReader<>(actionBodyType());
    }

    @Override
    protected ResponseWriter<AwsEventResponse, AwsEventResponse> responseWriter(AwsEventRequest request, Context context) {
        return new DummyResponseWriter<>();
    }

    @Override
    protected FunctionalUtils.UnsafeBiConsumer<EventActionRequest<T>, Context> loggerBeforeHandle() {
        return (r, c) ->
                logger.info("START {} with {}",
                    () -> this.getClass().getName(), () -> maskableJson(r.getBody()));
    }

    @Override
    protected FunctionalUtils.UnsafeBiConsumer<EventActionRequest<T>, Context> loggerAfterHandle() {
        return (r, c) -> logger.info("END {}", this.getClass().getName());
    }
}
