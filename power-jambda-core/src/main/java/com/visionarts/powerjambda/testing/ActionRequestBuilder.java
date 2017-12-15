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

package com.visionarts.powerjambda.testing;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.ApiGatewayRequestContext;
import com.visionarts.powerjambda.AwsProxyRequest;
import com.visionarts.powerjambda.http.HttpMethod;
import com.visionarts.powerjambda.models.ActionRequest;
import com.visionarts.powerjambda.utils.Utils;

/**
 *  for unittest
 *
 */
public class ActionRequestBuilder<T> {

    private AwsProxyRequest request;
    private T body;

    private final ObjectMapper om = Utils.getObjectMapper();

    public ActionRequestBuilder() {
        request = new AwsProxyRequest();
        request.setHeaders(new HashMap<>());
        request.setPathParameters(new HashMap<>());
        request.setQueryStringParameters(new HashMap<>());
        request.setStageVariables(new HashMap<>());
        request.setRequestContext(new ApiGatewayRequestContext());
    }

    public ActionRequestBuilder(AwsProxyRequest request) {
        this.request = request;
    }

    public ActionRequestBuilder<T> method(HttpMethod method) {
        request.setHttpMethod(method.name());
        return this;
    }

    public ActionRequestBuilder<T> stage(String stage) {
        request.getRequestContext().setStage(stage);
        return this;
    }

    public ActionRequestBuilder<T> path(String path) {
        request.setPath(path);
        return this;
    }

    public ActionRequestBuilder<T> header(String key, String value) {
        request.getHeaders().put(key, value);
        return this;
    }

    public ActionRequestBuilder<T> pathParameter(String key, String value) {
        request.getPathParameters().put(key, value);
        return this;
    }

    public ActionRequestBuilder<T> queryStringParameter(String key, String value) {
        request.getQueryStringParameters().put(key, value);
        return this;
    }

    public ActionRequestBuilder<T> stageVariable(String key, String value) {
        request.getStageVariables().put(key, value);
        return this;
    }

    public ActionRequestBuilder<T> bodyAsString(String body, Class<T> bodyClazz) {
        try {
            this.body = om.readValue(body, bodyClazz);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        request.setBody(body);
        return this;
    }

    public ActionRequestBuilder<T> body(T body) {
        this.body = body;
        return this;
    }

    public ActionRequest<T> build() {
        ActionRequest<T> newRequest = new ActionRequest<>(request);
        newRequest.setBody(body);
        return newRequest;
    }
}
