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

package com.visionarts.powerjambda;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.annotations.Route;
import com.visionarts.powerjambda.exceptions.DefaultGlobalExceptionHandler;
import com.visionarts.powerjambda.http.HttpMethod;
import com.visionarts.powerjambda.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * The class has the handler for Lambda Proxy Integration through a Proxy Resource.
 *
 */
public class ActionRouter extends AbstractRouter<AwsProxyRequest, AwsProxyResponse> {

    public ActionRouter(ApplicationContext applicationContext) {
        this(applicationContext, new DefaultGlobalExceptionHandler());
    }

    public ActionRouter(ApplicationContext applicationContext,
                        GlobalExceptionHandler<AwsProxyResponse> exceptionHandler) {
        super(applicationContext, exceptionHandler);
    }

    @Override
    public AwsProxyResponse apply(AwsProxyRequest request, Context context) {
        AwsProxyResponse response = super.apply(request, context);
        logger.info("Return the response code : {}", response.getStatusCode());
        // TODO How do we set CORS?
        response.addHeaderIfAbsent("Access-Control-Allow-Origin", "*");
        return response;
    }

    @Override
    protected Optional<Class<?>> findAction(String packagePath, AwsProxyRequest request) {
        logger.debug("resourcePath={}, method={}", () -> request.getRequestContext().getResourcePath(),
            () -> request.getHttpMethod());
        Set<Class<?>> actionClasses =
                ReflectionUtils.findAllClassesWithAnnotation(packagePath,
                    Route.class,
                    c -> StringUtils.equals(c.getAnnotation(Route.class).resourcePath(),
                             request.getRequestContext().getResourcePath()) &&
                             Arrays.asList(c.getAnnotation(Route.class).methods())
                                 .contains(HttpMethod.valueOf(request.getHttpMethod())));
        return actionClasses.stream()
                .findFirst();
    }

}
