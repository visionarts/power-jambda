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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.ResponseWriter;
import com.visionarts.powerjambda.annotations.ExceptionHandler;
import com.visionarts.powerjambda.models.ResponseEntity;
import com.visionarts.powerjambda.utils.OptionalUtils;
import com.visionarts.powerjambda.utils.ReflectionUtils;
import com.visionarts.powerjambda.utils.Utils;

/**
 * A ExceptionResolver that resolves the exception
 * through {@code @ExceptionHandler} methods in the action.
 *
 */
public class DefaultActionExceptionResolver<ResponseT> implements ExceptionResolver<ResponseT> {

    private ResponseWriter<ResponseEntity<?>, ResponseT> responseWriter;
    private Class<? extends Throwable> exceptionClazz;
    private Class<?> actionClazz;

    /**
     * Create new instance that resolves an exception.<br>
     * <br>
     *
     */
    public DefaultActionExceptionResolver(ResponseWriter<ResponseEntity<?>, ResponseT> responseWriter) {
        this.responseWriter = responseWriter;
    }

    /**
     * Handles an exception and returns the response using the ExceptionHandler method in the action.<br>
     * <br>
     *
     * @param action The action with exception handler
     * @param exception The raised exception
     * @return The exception as error response to API Gateway
     * @throws InternalErrorException The exception that occurred
     */
    @Override
    public <T> ResponseT handleException(Throwable exception, T action, Context context) throws InternalErrorException {
        this.exceptionClazz = exception.getClass();
        this.actionClazz = (Class<?>) action.getClass();
        try {
            return responseWriter.writeResponse(handle(exception, action, context));
        } catch (IOException e) {
            throw new InternalErrorException("Failed to write the error response", e);
        }
    }


    /**
     * Find an {@code @ExceptionHandler} method and invoke it to handle the raised exception.<br>
     * <br>
     */
    private <T> ResponseEntity<?> handle(Throwable targetException, T action, Context context)
            throws InternalErrorException {
        Method method = findExcepionHandler(targetException);
        Object retVal = invokeMethod(method, action, targetException, context);

        Utils.requireNonNull(retVal,
            () -> new InternalErrorException("Exception handler returns null"));
        return (ResponseEntity<?>) retVal;
    }

    private Method findExcepionHandler(Throwable exception) throws InternalErrorException {
        Optional<Method> handler = findExceptionHandler(actionClazz, exceptionClazz);
        // fallback exception handlers if exists
        handler = OptionalUtils.or(handler, () -> findExceptionHandler(actionClazz, Exception.class));
        handler = OptionalUtils.or(handler, () -> findExceptionHandler(actionClazz, Throwable.class));
        Method method = handler.orElseThrow(
            () -> new InternalErrorException(
                    "Not found any ExceptionHandler for " + exceptionClazz.getName(), exception));
        return method;
    }

    private Optional<Method> findExceptionHandler(Class<?> actionClazz,
            Class<? extends Throwable> exceptionClazz) {
        return ReflectionUtils.findAllMethodsWithAnnotation(actionClazz, ExceptionHandler.class)
                .stream()
                .filter(m -> Arrays.asList(m.getAnnotation(ExceptionHandler.class).value()).contains(exceptionClazz))
                .filter(m -> m.getReturnType().equals(ResponseEntity.class))
                .findFirst();
    }

    private <T> Object invokeMethod(Method handler, T action, Throwable targetException, Context context)
            throws InternalErrorException {
        Object[] args = new Object[] { targetException, context };
        Object ret = null;

        try {
            ret = handler.invoke(action, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new InternalErrorException("Method invocation error : " + handler.getName(), e);
        }

        return ret;
    }
}
