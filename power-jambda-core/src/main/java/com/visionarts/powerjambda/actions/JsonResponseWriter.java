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

import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.AwsProxyResponse;
import com.visionarts.powerjambda.ResponseWriter;
import com.visionarts.powerjambda.models.ResponseEntity;
import com.visionarts.powerjambda.utils.Utils;

public class JsonResponseWriter<T> extends ResponseWriter<T, AwsProxyResponse> {

    private final ObjectMapper om = Utils.getObjectMapper();

    @Override
    protected AwsProxyResponse writeEntity(T value) throws JsonProcessingException {
        return new AwsProxyResponse(200, Collections.emptyMap(),
                        om.writeValueAsString(value))
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    @Override
    protected AwsProxyResponse writeEntity(ResponseEntity<T> value) throws JsonProcessingException {
        return new AwsProxyResponse(value.getStatusCode(), value.getHeaders(),
                        om.writeValueAsString(value.getBody()))
                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }
}
