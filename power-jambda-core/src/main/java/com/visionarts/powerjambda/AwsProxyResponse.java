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
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The output format of Lambda function for 'aws_proxy' integration type on API Gateway.
 *
 */
public class AwsProxyResponse {

    private boolean isBase64Encoded;
    private int statusCode;
    private Map<String, String> headers;
    private String body;

    public AwsProxyResponse() {
        this(200);
    }

    public AwsProxyResponse(int statusCode) {
        this(statusCode, Collections.emptyMap());
    }

    public AwsProxyResponse(int statusCode, Map<String, String> headers) {
        this(statusCode, headers, null);
    }

    public AwsProxyResponse(int statusCode, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.headers.putAll(headers != null ? headers : Collections.emptyMap());
        this.body = body;
    }

    @JsonProperty("isBase64Encoded")
    public boolean isBase64Encoded() {
        return isBase64Encoded;
    }

    public void setBase64Encoded(boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    @JsonIgnore
    public AwsProxyResponse addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    @JsonIgnore
    public AwsProxyResponse addHeaderIfAbsent(String key, String value) {
        this.headers.putIfAbsent(key, value);
        return this;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
