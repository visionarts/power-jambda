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

import java.io.IOException;

import com.visionarts.powerjambda.models.ResponseEntity;

public abstract class ResponseWriter<I, O> {

    public O writeResponse(I value) throws IOException {
        if (value instanceof ResponseEntity) {
            @SuppressWarnings("unchecked")
            ResponseEntity<I> val = (ResponseEntity<I>)value;
            return writeEntity(val);
        } else {
            return writeEntity(value);
        }
    }

    protected abstract O writeEntity(I value) throws IOException;
    protected abstract O writeEntity(ResponseEntity<I> value) throws IOException;
}
