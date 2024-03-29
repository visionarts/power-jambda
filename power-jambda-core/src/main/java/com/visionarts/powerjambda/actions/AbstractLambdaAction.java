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

package com.visionarts.powerjambda.actions;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.AwsProxyRequest;
import com.visionarts.powerjambda.AwsProxyResponse;
import com.visionarts.powerjambda.RequestReader;
import com.visionarts.powerjambda.ResponseWriter;
import com.visionarts.powerjambda.annotations.Route;
import com.visionarts.powerjambda.cors.NoneCorsConfiguration;
import com.visionarts.powerjambda.exceptions.DefaultActionExceptionResolver;
import com.visionarts.powerjambda.exceptions.InternalErrorException;
import com.visionarts.powerjambda.filters.CorsFilter;
import com.visionarts.powerjambda.filters.FilterChain;
import com.visionarts.powerjambda.models.ActionRequest;
import com.visionarts.powerjambda.utils.FunctionalUtils;
import com.visionarts.powerjambda.utils.FunctionalUtils.UnsafeBiConsumer;

/**
 * Abstract class for all action for RESTful API.<br>
 *
 * @param <T> The type of request body
 * @param <ActionResultT> The type of action result to return the response
 */
public abstract class AbstractLambdaAction<T, ActionResultT>
        extends LambdaBaseAction<AwsProxyRequest, AwsProxyResponse, ActionRequest<T>, ActionResultT> {

    protected final FilterChain<AwsProxyResponse> filterChain = new FilterChain<>();

    public AbstractLambdaAction() {
        Class<?> c = this.getClass();
        Route route = c.getAnnotation(Route.class);
        if (route.cors() != NoneCorsConfiguration.class) {
            filterChain.addFilter(new CorsFilter(route.cors()));
        }
    }

    /**
     * Returns the type of the class modeled by the body type in the action request. <br>
     * <p>
     * For deserializing body in the action request.
     *
     * @return The body type in the action request
     */
    public abstract Class<T> actionBodyType();

    @Override
    protected RequestReader<AwsProxyRequest, ActionRequest<T>> requestReader(AwsProxyRequest request, Context context) {
        return new JsonBodyActionRequestReader<>(actionBodyType());
    }

    @Override
    protected ResponseWriter<ActionResultT, AwsProxyResponse> responseWriter(AwsProxyRequest request, Context context) {
        return new JsonResponseWriter<>();
    }

    @Override
    protected final void beforeHandle(ActionRequest<T> actionRequest, Context context) throws Exception {
        FunctionalUtils.toBiConsumer(loggerBeforeHandle(), logger)
                .accept(actionRequest, context);
        beforeAction(actionRequest, context);
    }

    @Override
    protected final void afterHandle(ActionRequest<T> actionRequest, Context context) {
        try {
            afterAction(actionRequest, context);
        } finally {
            FunctionalUtils.toBiConsumer(loggerAfterHandle(), logger)
                    .accept(actionRequest, context);
        }
    }

    @Override
    public AwsProxyResponse handleException(Throwable exception, Context context)
            throws InternalErrorException {
        DefaultActionExceptionResolver<AwsProxyResponse> resolver =
                new DefaultActionExceptionResolver<>(new JsonResponseWriter<>());
        return applyFilters(resolver.handleException(exception, this, context));
    }

    /**
     * Applies filters to the response object.
     *
     * @param response to filter
     * @return The response to be applied filters
     */
    @Override
    protected AwsProxyResponse applyFilters(AwsProxyResponse response) {
        filterChain.filter(response);
        return response;
    }

    @Override
    protected UnsafeBiConsumer<ActionRequest<T>, Context> loggerBeforeHandle() {
        return (r, c) ->
            logger.info("Starting {} method: {} path: {} params: {} body: {} ({})",
                () -> this.getClass().getName(), r::getMethod, r::getPath, r::getPathParameters,
                () -> actionBodyType().getName(), () -> maskableJson(r.getBody()));
    }

    @Override
    protected UnsafeBiConsumer<ActionRequest<T>, Context> loggerAfterHandle() {
        return (r, c) -> logger.info("End {}", this.getClass().getName());
    }

}
