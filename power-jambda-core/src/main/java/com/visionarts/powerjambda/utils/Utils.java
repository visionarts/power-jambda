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

package com.visionarts.powerjambda.utils;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generic utilities.
 */
public class Utils {

    private static final ObjectMapper defaultMapper =
            new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    // Suppresses default constructor, ensuring non-instantiability.
    private Utils() {
    }

    /**
     * Returns default configured ObjectMapper instance.
     * @return ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return defaultMapper;
    }

    /**
     * Checks that the specified object is satisfied the given condition.
     *
     * @param <T> Type of the object to be check
     * @param <E> Type of the exception to be thrown
     * @param obj The object reference to check for the condition
     * @param condition The condition to be satisfied
     * @param exceptionSupplier The supplier which will return the exception to
     *            be thrown
     * @return {@code obj}
     * @throws E If the object is not satisfied the condition
     */
    public static <T, E extends Throwable> T require(T obj, Predicate<T> condition,
            Supplier<? extends E> exceptionSupplier) throws E {
        Objects.requireNonNull(condition);
        Objects.requireNonNull(exceptionSupplier);
        if (!condition.test(obj)) {
            throw exceptionSupplier.get();
        }
        return obj;
    }

    /**
     * Checks that the specified object is not {@code null} and
     * throws a customized exception.
     *
     * @param <T> Type of the object to be check
     * @param <E> Type of the exception to be thrown
     * @param obj The object to check for nullity
     * @param exceptionSupplier The supplier which will return the exception to be thrown
     * @return {@code obj} if not {@code null}
     * @throws E If {@code obj} is {@code null}
     */
    public static <T, E extends Throwable> T requireNonNull(T obj,
            Supplier<? extends E> exceptionSupplier) throws E {
        return require(obj, Objects::nonNull, exceptionSupplier);
    }
}
