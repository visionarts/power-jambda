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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;



/**
 * Helper class for getting environment variables on AWS Lambda runtime.<br>
 * <br>
 *
 */
public class EnvironmentVariableUtils {

    /**
     * An operation that accepts three input arguments and returns no result.<br>
     * <br>
     *
     * @param <T> The first input argument
     * @param <R> The second input argument
     * @param <S> The third input argument
     */
    @FunctionalInterface
    public static interface TriConsumer<T, R, S> {
        /**
         * Performs the operation given the specified arguments.<br>
         * <br>
         *
         * @param t The first input argument
         * @param r The second input argument
         * @param s The third input argument
         */
        void accept(T t, R r, S s);
    }

    private static final Map<Class<?>, TriConsumer<Field, Object, String>> FieldSetConsumerMap = new HashMap<>(8);
    static {
        FieldSetConsumerMap.put(Boolean.class, (f, o, v) -> ReflectionUtils.fieldSetAsBoolean(f, o, v));
        FieldSetConsumerMap.put(boolean.class, (f, o, v) -> ReflectionUtils.fieldSetAsBoolean(f, o, v));
        FieldSetConsumerMap.put(String.class, (f, o, v) -> ReflectionUtils.fieldSetAsString(f, o, v));
        FieldSetConsumerMap.put(Integer.class, (f, o, v) -> ReflectionUtils.fieldSetAsInteger(f, o, v));
        FieldSetConsumerMap.put(int.class, (f, o, v) -> ReflectionUtils.fieldSetAsInteger(f, o, v));
    }

    // Suppresses default constructor, ensuring non-instantiability.
    private EnvironmentVariableUtils() {
    }

    /**
     * Instantiates the given configuration class from Environment variables on AWS Lambda.<br>
     * <br>
     * Note: The configuration class must have the following rules. <br>
     *       - Need a constructor with no arguments <br>
     *       - The field type is public and available in [Boolean/boolean/String/Integer/int] <br>
     *       - The modifier of the field must not be final <br>
     *       - Search field's name from Environment variables when getting an Environment value <br>
     *
     * @param <C> The configuration class
     * @param configurationClazz The configuration class to instantiate
     * @return The new configuration instance
     */
    public static <C> C newConfigurationInstance(Class<C> configurationClazz) {
        Objects.requireNonNull(configurationClazz);
        Utils.require(configurationClazz,
                FunctionalUtils.not(Class<C>::isInterface),
                () -> new IllegalArgumentException("Specified class is an interface : " + configurationClazz.toString()));

        try {
            C configuration = configurationClazz.getConstructor().newInstance();
            Arrays.stream(configurationClazz.getFields())
                .forEach(field -> fieldSetFromEnvVal(field, configuration));
            return configuration;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void fieldSetFromEnvVal(Field field, Object targetObject) {
        String value = System.getenv(field.getName());
        if (value == null) { // skip if not exist on environment
            return;
        }

        Optional.ofNullable(FieldSetConsumerMap.get(field.getType()))
            .orElseThrow(() ->
                new UnsupportedOperationException(
                        "Not supported type : " + field.getType().getSimpleName() +
                        ", avaialbe in [Boolean/boolean/String/Integer/int]"))
            .accept(field, targetObject, value);
    }
}
