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

package com.visionarts.powerjambda.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.visionarts.powerjambda.ResponseWriter;
import com.visionarts.powerjambda.models.ResponseEntity;

/**
 * This response writer class ignores a response from action,
 * and always returns {@link AwsEventResponse#AWS_EVENT_SUCCESSFUL_RESPONSE}
 *
 */
public class DummyResponseWriter<T> extends ResponseWriter<T, AwsEventResponse> {

    @Override
    protected AwsEventResponse writeEntity(T value) throws JsonProcessingException {
        // regard as successful
        return AwsEventResponse.AWS_EVENT_SUCCESSFUL_RESPONSE;
    }

    @Override
    protected AwsEventResponse writeEntity(ResponseEntity<T> value) throws JsonProcessingException {
        // regard as successful
        return AwsEventResponse.AWS_EVENT_SUCCESSFUL_RESPONSE;
    }
}
