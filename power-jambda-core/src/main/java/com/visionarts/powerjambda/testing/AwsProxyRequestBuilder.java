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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.ApiGatewayRequestContext;
import com.visionarts.powerjambda.AwsProxyRequest;
import com.visionarts.powerjambda.http.HttpMethod;
import com.visionarts.powerjambda.utils.Utils;

/**
 *  for unittest
 *
 */
public class AwsProxyRequestBuilder {

    private AwsProxyRequest request;
    private final ObjectMapper om = Utils.getObjectMapper();


    public AwsProxyRequestBuilder() {
        this(null, null);
    }

    public AwsProxyRequestBuilder(InputStream jsonStream) throws IOException {
        fromJsonStream(jsonStream);
    }

    public AwsProxyRequestBuilder(String path, String httpMethod) {
        request = new AwsProxyRequest();
        request.setPath(path);
        request.setHttpMethod(httpMethod);
        ApiGatewayRequestContext context = new ApiGatewayRequestContext();
        context.setHttpMethod(httpMethod);
        request.setRequestContext(context);
    }

    public AwsProxyRequestBuilder path(String path) {
        this.request.setPath(path);
        return this;
    }

    public AwsProxyRequestBuilder httpMethod(String httpMethod) {
        this.request.setHttpMethod(httpMethod);
        this.request.getRequestContext().setHttpMethod(httpMethod);
        if (HttpMethod.GET == HttpMethod.valueOf(httpMethod)) {
            body((String) null);
        }
        return this;
    }

    public AwsProxyRequestBuilder header(String key, String value) {
        this.request.getHeaders().put(key, value);
        return this;
    }

    public AwsProxyRequestBuilder body(String body) {
        this.request.setBody(body);
        return this;
    }

    public AwsProxyRequestBuilder body(Object body) {
        try {
            body(om.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            throw new UnsupportedOperationException(e);
        }
        return this;
    }

    public AwsProxyRequestBuilder resourcePath(String path) {
        this.request.getRequestContext().setResourcePath(path);
        return this;
    }

    public AwsProxyRequestBuilder fromJsonStream(InputStream jsonStream) throws IOException {
        request = om.readValue(jsonStream, AwsProxyRequest.class);
        return this;
    }

    public AwsProxyRequest build() {
        return request;
    }

    public InputStream buildAsStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        om.writeValue(out, request);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
