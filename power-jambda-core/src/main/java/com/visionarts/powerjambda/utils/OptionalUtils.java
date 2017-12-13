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

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Provides utility methods for {@link java.util.Optional} class. <br>
 * <br>
 */
public final class OptionalUtils {

    // Suppresses default constructor, ensuring non-instantiability.
    private OptionalUtils() {
    }

    /**
     * Returns the {@code optional} instance
     * if a value is present in it; supplier.get() otherwise.<br>
     * <br>
     * As of Java 9 you can do simply<br>
     * <pre>{@code
     *     firstOptional.or(() -> secondOptional);
     * }</pre>
     *
     * @param <T> The class of the value
     * @param optional An Optional to check
     * @param supplier A Supplier whose result is returned if no value is present
     * @return An {@code Optional} with the value present
     */
    public static <T> Optional<T> or(Optional<T> optional, Supplier<Optional<T>> supplier) {
        Optional<T> res;
        if (optional.isPresent()) {
            res = optional;
        } else {
            res = supplier.get();
        }
        return res;
    }
}
