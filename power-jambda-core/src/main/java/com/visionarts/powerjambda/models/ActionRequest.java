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

package com.visionarts.powerjambda.models;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.visionarts.powerjambda.AwsProxyRequest;
import com.visionarts.powerjambda.http.HttpMethod;

/**
 * The request to populate an action.
 *
 * @param <T> The type of body in an action request
 *
 */
public class ActionRequest<T> {

    private AwsProxyRequest request;
    private T body;

    public ActionRequest() {
        this(null);
    }

    public ActionRequest(AwsProxyRequest request) {
        this.request = request;
    }

    public AwsProxyRequest getOriginalRequest() {
        return request;
    }

    /**
     * Returns HTTP method.
     *
     * @return HTTP method
     */
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(request.getHttpMethod());
    }

    /**
     * Returns the name of deployment stage on API Gateway.
     *
     * @return The name of stage
     */
    public String getStage() {
        return request.getRequestContext().getStage();
    }

    /**
     * Returns the stage variables defined for the stage in API Gateway.
     *
     * @return The stage variables
     */
    public Map<String, String> getStageVariables() {
        return request.getStageVariables();
    }

    /**
     * Returns the stage variable defined for the stage in API Gateway.
     *
     * @param key The key
     * @return The stage variable
     */
    public String getStageVariable(String key) {
        return request.getStageVariables().get(key);
    }

    public String getPath() {
        return request.getPath();
    }

    public Map<String, String> getHeaders() {
        return request.getHeaders();
    }

    public String getHeader(String key) {
        return request.getHeaders().get(key);
    }

    public Map<String, String> getPathParameters() {
        return Optional.ofNullable(request.getPathParameters())
                .orElse(Collections.emptyMap());
    }

    public String getPathParameter(String key) {
        return getPathParameters().get(key);
    }

    public Map<String, String> getQueryParameters() {
        return Optional.ofNullable(request.getQueryStringParameters())
                .orElse(Collections.emptyMap());
    }

    public String getQueryParameter(String key) {
        return getQueryParameters().get(key);
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getBodyAsString() {
        return request.getBody();
    }
}
