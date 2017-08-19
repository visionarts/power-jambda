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
 * The exception represents Client Errors(HTTP Status Codes - 4xx).<br>
 * <br>
 */
public class ClientErrorException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_STATUS_CODE = 400;

    private int statusCode;

    public ClientErrorException(String message) {
        this(DEFAULT_STATUS_CODE, message);
    }

    public ClientErrorException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ClientErrorException(String message, Throwable cause) {
        this(DEFAULT_STATUS_CODE, message, cause);
    }

    public ClientErrorException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
