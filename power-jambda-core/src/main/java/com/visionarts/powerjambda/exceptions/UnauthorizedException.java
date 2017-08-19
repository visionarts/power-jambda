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

/**
 * Thrown if an authentication request is rejected. <br>
 * <br>
 *
 */
public class UnauthorizedException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs new UnauthorizedException instance with the given
     * message.
     *
     * @param message The detail message
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Constructs new UnauthorizedException instance with the given message
     * and root cause.
     *
     * @param message The detail message
     * @param cause The root cause
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
