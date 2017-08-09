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
package com.visionarts.powerjambda.exceptions;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.AwsProxyResponse;
import com.visionarts.powerjambda.GlobalExceptionHandler;
import com.visionarts.powerjambda.models.DefaultErrorResponseBody;
import com.visionarts.powerjambda.utils.Utils;

/**
 * Default exception handler for framework.<br>
 * <br>
 */
public class DefaultGlobalExceptionHandler implements GlobalExceptionHandler<AwsProxyResponse>{

    private static final Logger logger = LogManager.getLogger(DefaultGlobalExceptionHandler.class);
    private static final ObjectMapper om = Utils.getObjectMapper();
    private static final Map<String, String> headers = new HashMap<>();
    static {
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    public AwsProxyResponse handleException(Exception ex, Context context) {
        logger.error("Catching Internal Server Error at default exception handler", ex);

        DefaultErrorResponseBody body =
                new DefaultErrorResponseBody("Internal Server Error", ex.getMessage());
        return new AwsProxyResponse(500, headers, writeValueAsString(body));
    }

    public AwsProxyResponse handleException(ClientErrorException ex, Context context) {
        logger.error("Catching Client Error at default exception handler", ex);

        DefaultErrorResponseBody body =
                new DefaultErrorResponseBody("Client Error", ex.getMessage());
        return new AwsProxyResponse(ex.getStatusCode(), headers, writeValueAsString(body));
    }

    private String writeValueAsString(Object value) {
        try {
            return om.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
