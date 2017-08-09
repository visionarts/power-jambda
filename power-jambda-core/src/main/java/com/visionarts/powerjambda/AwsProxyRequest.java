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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The input format of Lambda function for 'aws_proxy' integration type on API Gateway.
 *
 */
public class AwsProxyRequest {

    /** Resource */
    private String resource;
    /** Resource path in a request URL */
    private String path;
    /** HTTP method */
    private String httpMethod;
    /** HTTP headers */
    private Map<String, String> headers;
    /** Query parameters */
    private Map<String, String> queryStringParameters;
    /** Path parameters specified at API Gateway */
    private Map<String, String> pathParameters;
    /** The set of stage variables defined at API Gateway */
    private Map<String, String> stageVariables;
    /** A JSON string of the request payload */
    private String body;
    /** A boolean flag to indicate if the applicable request payload is Base64-encode */
    private boolean isBase64Encoded;
    /** Request context issued by API Gateway */
    private ApiGatewayRequestContext requestContext;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.headers.putAll(headers != null ? headers : Collections.emptyMap());
    }

    public Map<String, String> getQueryStringParameters() {
        return queryStringParameters;
    }

    public void setQueryStringParameters(Map<String, String> queryStringParameters) {
        this.queryStringParameters = queryStringParameters;
    }

    public Map<String, String> getPathParameters() {
        return pathParameters;
    }

    public void setPathParameters(Map<String, String> pathParameters) {
        this.pathParameters = pathParameters;
    }

    public Map<String, String> getStageVariables() {
        return stageVariables;
    }

    public void setStageVariables(Map<String, String> stageVariables) {
        this.stageVariables = stageVariables;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }

    @JsonProperty("isBase64Encoded")
    public void setBase64Encoded(Boolean isBase64Encoded) {
        this.isBase64Encoded = Optional.ofNullable(isBase64Encoded).orElse(false);
    }

    public ApiGatewayRequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(ApiGatewayRequestContext requestContext) {
        this.requestContext = requestContext;
    }
}
