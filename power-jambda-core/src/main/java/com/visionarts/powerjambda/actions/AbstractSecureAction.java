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
package com.visionarts.powerjambda.actions;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.annotations.ExceptionHandler;
import com.visionarts.powerjambda.exceptions.UnauthorizedException;
import com.visionarts.powerjambda.models.ActionRequest;
import com.visionarts.powerjambda.models.DefaultErrorResponseBody;
import com.visionarts.powerjambda.models.ResponseEntity;


/**
 * This abstract class provides a method to authenticate client.<br>
 * All action classes with the credentials must inherit this abstract class.<br>
 * <br>
 * @param <T> The type of request body
 * @param <ActionResult> The type of action result to return the response
 * @param <Credentials> The type of client credentials
 */
public abstract class AbstractSecureAction<T, ActionResult, Credentials> extends AbstractLambdaAction<T, ActionResult> {

    /**
     * Authenticates and returns a credential when the authentication is successful.<br>
     * <br>
     *
     * @param request The action request
     * @param context The Lambda context passed by the AWS Lambda environment
     * @return The client credentials object if the authentication is successful
     * @throws UnauthorizedException Thrown if an authentication request is rejected
     * @throws Exception Thrown if unknown error occurs
     */
    protected abstract Credentials authenticate(ActionRequest<T> request, Context context)
            throws UnauthorizedException, Exception;

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<DefaultErrorResponseBody> exceptionHandler(UnauthorizedException ex, Context context) {
        logger.error("Catching Unauthorized Error at default exception handler", ex);

        DefaultErrorResponseBody body = new DefaultErrorResponseBody("Unauthorized Error", ex.getMessage());
        return new ResponseEntity<DefaultErrorResponseBody>()
                    .statusCode(401)
                    .body(body);
    }
}
