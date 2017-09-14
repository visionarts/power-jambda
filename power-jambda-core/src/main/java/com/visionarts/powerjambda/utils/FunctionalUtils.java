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

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.logging.log4j.Logger;

/**
 * Provides utility methods for Functional interfaces. <br>
 * <br>
 */
public class FunctionalUtils {

    // Suppresses default constructor, ensuring non-instantiability.
    private FunctionalUtils() {
    }

    /**
     * Null-safe check if the specified collection is empty. <br>
     * <br>
     * Null returns true.
     *
     * @param <T> The type of collection elements
     * @param collection The collection to be check, maybe null
     * @return True if {@code null} or empty
     */
    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Null-safe check if the specified collection is not empty. <br>
     * <br>
     * Null returns false.
     *
     * @param <T> The type of collection elements
     * @param collection The collection to be check, maybe null
     * @return True if non-{@code null} and non-empty
     *
     * @apiNote This method exists to be used as a
     *          {@link java.util.function.Predicate} ,
     *          {@code filter(FunctionalUtil::isNotEmpty)}
     *
     * @see java.util.function.Predicate
     */
    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    /**
     * Null-safe check if the specified string is empty. <br>
     * <br>
     * Null returns true.
     *
     * @param string The collection to be check, maybe null
     * @return True if {@code null} or empty
     */
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Null-safe check if the specified string is not empty. <br>
     * <br>
     * Null returns false.
     *
     * @param string The string to be check, maybe null
     * @return True if non-{@code null} and non-empty
     *
     * @apiNote This method exists to be used as a
     *          {@link java.util.function.Predicate} ,
     *          {@code filter(FunctionalUtil::isNotEmpty)}
     *
     * @see java.util.function.Predicate
     */
    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    /**
     * Returns a predicate that represents the logical negation of specified
     * predicate.
     *
     * @param <T> The type of arguments to the predicate
     * @param predicate The predicate to negate
     * @return A predicate that represents the logical negation of specified
     * predicate
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        return predicate.negate();
    }

    /**
     * A wrapper around a Consumer that throws a checked exception.
     *
     * @param unsafeConsumer Something that acts like a consumer but may throw an exception
     * @param logger Logger that used to log if an exception thrown
     * @return A consumer that is wrapped in a try-catch converting the checked exception into a runtime exception
     */
    public static <T> Consumer<T> toConsumer(UnsafeConsumer<T> unsafeConsumer, Logger logger) {
        return t -> {
            try {
                unsafeConsumer.accept(t);
            } catch (Exception e) {
                throw logger.throwing(new RuntimeException(e));
            }
        };
    }

    /**
     * A wrapper around a Consumer that throws a checked exception.
     *
     * @param unsafeBiConsumer Something that acts like a consumer but may throw an exception
     * @param logger Logger that used to log if an exception thrown
     * @return A consumer that is wrapped in a try-catch converting the checked exception into a runtime exception
     */
    public static <T, U> BiConsumer<T, U> toBiConsumer(UnsafeBiConsumer<T, U> unsafeBiConsumer, Logger logger) {
        return (t, u) -> {
            try {
                unsafeBiConsumer.accept(t, u);
            } catch (Exception e) {
                throw logger.throwing(new RuntimeException(e));
            }
        };
    }

    /**
     * Equivalent of {@link Consumer} that may throw a checked exception.
     *
     */
    @FunctionalInterface
    public interface UnsafeConsumer<T> {
        void accept(T t) throws Exception;
    }

    /**
     * Equivalent of {@link BiConsumer} that may throw a checked exception.
     *
     */
    @FunctionalInterface
    public interface UnsafeBiConsumer<T, U> {
        void accept(T t, U u) throws Exception;
    }
}
