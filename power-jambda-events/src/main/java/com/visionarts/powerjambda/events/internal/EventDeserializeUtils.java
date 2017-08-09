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
package com.visionarts.powerjambda.events.internal;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Intrenal use only
 *
 */
public class EventDeserializeUtils {

    /**
     * The object mapper for the AWS Event. <br>
     * <br>
     */
    private static final ObjectMapper AWS_EVENT_MAPPER =
            new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    // Suppresses default constructor, ensuring non-instantiability.
    private EventDeserializeUtils() {
        // nothing to do here
    }

    public static <T> Optional<T> resolveAWSEvent(final byte[] input, Class<T> eventClazz,
            Predicate<T> eventCondition) throws IOException {
        return resolveEvent(input, eventClazz, eventCondition, AWS_EVENT_MAPPER);
    }

    public static <T> Optional<T> resolveEvent(final byte[] input, Class<T> eventClazz,
            Predicate<T> eventCondition, ObjectMapper om) throws IOException {
        Objects.requireNonNull(input);
        Objects.requireNonNull(eventClazz);
        Objects.requireNonNull(eventCondition);
        Objects.requireNonNull(om);
        T event = om.readValue(input, eventClazz);
        if (eventCondition.test(event)) {
            return Optional.of(event);
        }
        return Optional.empty();
    }
}
