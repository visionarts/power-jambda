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
package com.visionarts.powerjambda;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.actions.LambdaBaseAction;
import com.visionarts.powerjambda.exceptions.ClientErrorException;
import com.visionarts.powerjambda.exceptions.InternalErrorException;


/**
 * The abstract class selects a property action to match with a request,<br>
 * and returns a response.
 *
 * @param <Request> The type of request to Lambda function
 * @param <Response> The type of response from Lambda function
 */
public abstract class AbstractRouter<Request, Response> implements Router<Request, Response> {

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    private final ApplicationContext applicationContext;
    private final GlobalExceptionHandler<Response> exceptionHandler;

    public AbstractRouter(ApplicationContext applicationContext,
                          GlobalExceptionHandler<Response> exceptionHandler) {
        this.applicationContext = applicationContext;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Handles the given request, and returns the response.
     *
     * @param request
     *            The incoming request
     * @param context
     *            The Lambda Context object passed by the AWS Lambda environment
     * @return The response returned by the action class
     */
    @Override
    public Response apply(Request request, Context context) {
        Response response;
        try {
            response = handle(request, context);
        } catch (ClientErrorException e) {
            response = exceptionHandler.handleException(e, context);
        } catch (Exception e) {
            response = exceptionHandler.handleException(e, context);
        }
        return response;
    }

    private final Response handle(Request request, Context context)
            throws ClientErrorException, InternalErrorException {

        logger.traceEntry();
        LambdaBaseAction<Request, Response, ?, ?> action;
        try {
            action = instantiateAction(request);
        } catch (ReflectiveOperationException e) {
            throw new InternalErrorException("Error while instantiating action class", e);
        } catch (ClientErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalErrorException("Unknown error while instantiating action class", e);
        }

        // invoke the action handler
        try {
            return action.handleRequest(request, context);
        } catch (ClientErrorException e) {
            throw e;
        } catch (Exception raisedException) {
            logger.debug("The exception have occurred while processing action class : {}", raisedException.getMessage());
            logger.debug("Exception detail", raisedException);

            return action.handleException(raisedException, context);
        } finally {
            logger.traceExit();
        }
    }

    /**
     * Instantiates the action object to match with a request.
     *
     * @param request
     * @return The action object
     * @throws ReflectiveOperationException
     * @throws ClientErrorException
     */
    private LambdaBaseAction<Request, Response, ?, ?> instantiateAction(Request request)
            throws ReflectiveOperationException, ClientErrorException {
        Class<?> actionClazz = findAction(
                applicationContext.getApplicationMainClass().getPackage().getName(), request)
                .orElseThrow(() -> new ClientErrorException(404, "Action not found"));
        @SuppressWarnings("unchecked")
        LambdaBaseAction<Request, Response, ?, ?> act = LambdaBaseAction.class.cast(actionClazz.newInstance());
        return act;
    }

    /**
     * Returns the action class object within a package path.
     *
     * @param packagePath The package path where {@link AbstractRouter} scans actions
     * @param request
     * @return An {@link java.util.Optional} object wrapped an action class object
     */
    protected abstract Optional<Class<?>> findAction(String packagePath, Request request);

}
