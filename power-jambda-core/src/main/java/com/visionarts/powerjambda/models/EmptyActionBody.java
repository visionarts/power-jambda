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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents an empty request body using HTTP GET or else methods.<br>
 * <br>
 */
@JsonInclude(Include.NON_NULL)
public final class EmptyActionBody {

    @JsonCreator
    public static EmptyActionBody valueOf(String jsonString) {
        return new EmptyActionBody();
    }

    /**
     * Returns a string representation of this object; useful for testing and debugging.
     *
     * @return A string representation of this object
     */
    @Override
    @JsonValue
    public String toString() {
        // add space character for empty json,
        // avoid confusion when Log4j2 logs a message with parameters placeholder('{}').
        return "{ }";
    }
}
