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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.reflections.Reflections;

/**
 * Reflection utilities.
 */
public class ReflectionUtils {

    // Suppresses default constructor, ensuring non-instantiability.
    private ReflectionUtils() {
    }

    public static Set<Method> findAllMethodsWithAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotation) {
        Method[] methods = clazz.getMethods();
        return Arrays.stream(methods)
                .filter(m -> Objects.nonNull(m.getAnnotation(annotation)))
                .collect(Collectors.toSet());
    }

    /**
     * Returns classes annotated with a given annotation.
     *
     * @param packagePrefix The location to find classes
     * @param annotation The annotation to find
     * @return The set of classes with a given annotation
     */
    public static Set<Class<?>> findAllClassesWithAnnotation(
            String packagePrefix,
            Class<? extends Annotation> annotation) {
        Objects.requireNonNull(packagePrefix);
        Objects.requireNonNull(annotation);
        Reflections reflections = new Reflections(packagePrefix);
        return reflections.getTypesAnnotatedWith(annotation);
    }

    /**
     * Returns classes annotated with a given annotation and condition.
     *
     * @param packagePrefix The location to find classes
     * @param annotation The annotation to find
     * @param matchCondition The condition to match
     * @return The set of classes with a given annotation and condition
     */
    public static Set<Class<?>> findAllClassesWithAnnotation(
            String packagePrefix,
            Class<? extends Annotation> annotation,
            Predicate<Class<?>> matchCondition) {
        Objects.requireNonNull(packagePrefix);
        Objects.requireNonNull(annotation);
        Objects.requireNonNull(matchCondition);
        Set<Class<?>> annotated = findAllClassesWithAnnotation(packagePrefix, annotation);
        return annotated.stream()
                .filter(matchCondition)
                .collect(Collectors.toSet());
    }

    public static void fieldSetAsBoolean(Field field, Object targetObject, String value) {
        fieldSet(field, targetObject, Boolean.valueOf(value));
    }

    public static void fieldSetAsString(Field field, Object targetObject, String value) {
        fieldSet(field, targetObject, value);
    }

    public static void fieldSetAsInteger(Field field, Object targetObject, String value) {
        fieldSet(field, targetObject, Integer.valueOf(value));
    }

    private static <V> void fieldSet(Field field, Object targetObject, V value) {
        try {
            field.set(targetObject, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
