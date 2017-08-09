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
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a response entity that has status code, HTTP header, body.
 *
 * @param <T> The type of response body
 */
public class ResponseEntity<T> {

    private int statusCode;
    private Map<String, String> headers;
    private T body;

    public ResponseEntity() {
        this(200, Collections.emptyMap(), null);
    }

    public ResponseEntity(int statusCode, Map<String, String> headers, T body) {
        this.statusCode = statusCode;
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.headers.putAll(headers != null ? headers : Collections.emptyMap());
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @JsonIgnore
    public ResponseEntity<T> statusCode(int statusCode) {
        setStatusCode(statusCode);
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @JsonIgnore
    public ResponseEntity<T> header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @JsonIgnore
    public ResponseEntity<T> body(T body) {
        setBody(body);
        return this;
    }

}
