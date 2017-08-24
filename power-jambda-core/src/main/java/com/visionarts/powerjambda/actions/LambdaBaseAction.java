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

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.visionarts.maskingjson.MaskableObjectMapper;
import com.visionarts.powerjambda.ActionExceptionHandler;
import com.visionarts.powerjambda.LambdaAction;
import com.visionarts.powerjambda.RequestReader;
import com.visionarts.powerjambda.ResponseWriter;
import com.visionarts.powerjambda.exceptions.ClientErrorException;
import com.visionarts.powerjambda.exceptions.InternalErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This abstract class implementing the LambdaAction interface.<br>
 * All action classes must inherit this abstract class.<br>
 * <br>
 * @param <ActionRequestT> The type of the action request
 * @param <ActionResultT> The type of the action result value to return the response
 */
public abstract class LambdaBaseAction<RequestT, ResponseT, ActionRequestT, ActionResultT>
        implements LambdaAction<ActionRequestT, ActionResultT>, ActionExceptionHandler<ResponseT> {

    private static final MaskableObjectMapper loggingObjectMapper = new MaskableObjectMapper();

    protected final Logger logger = LogManager.getLogger(this.getClass().getName());

    private RequestReader<RequestT, ActionRequestT> requestReader;
    private ResponseWriter<ActionResultT, ResponseT> responseWriter;
    private ActionRequestT actionRequest;


    public final ResponseT handleRequest(RequestT request, Context context) throws Exception {
        initialize(request, context);

        beforeHandle(actionRequest, context);
        ActionResultT result;
        try {
            result = handle(actionRequest, context);
            logger.info("Returned the action result {}", () -> maskableJson(result));
        } finally {
            afterHandle(actionRequest, context);
        }

        return writeResponse(result);
    }

    private void initialize(RequestT request, Context context) throws ClientErrorException {
        try {
            this.requestReader = requestReader(request, context);
            this.responseWriter = responseWriter(request, context);
            this.actionRequest = requestReader.readRequest(request);
        } catch (IOException e) {
            throw new ClientErrorException("Unexpected exception during reading a request", e);
        }
    }

    private ResponseT writeResponse(ActionResultT result) throws InternalErrorException {
        try {
            return responseWriter.writeResponse(result);
        } catch (IOException e) {
            throw new InternalErrorException("Unexpected exception during writing the response", e);
        }
    }

    /**
     * Convenience method for logging an object with mask.
     *
     * @param object to jsonify with mask
     * @return A json string, returns null if error occurs
     */
    protected final String maskableJson(Object object) {
        try {
            return loggingObjectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.warn("Unexpected exception during serializing an object for masking", e);
            return null; // go through
        }
    }

    /**
     * Called before an 'action' invocation.
     *
     * <p> Note that the action handler will skip if any exceptions occurs during {@code beforeAction} method.
     *
     * @throws Exception Thrown if an error in processing
     */
    protected abstract void beforeHandle(ActionRequestT actionRequest, Context context) throws Exception;

    /**
     * Called after an 'action' invocation.
     *
     */
    protected abstract void afterHandle(ActionRequestT actionRequest, Context context) throws Exception;

    /**
     * Returns a {@link RequestReader} object for producing a populated request for the action.
     *
     * @param request The incoming request object
     * @param context The AWS Lambda context
     * @return A {@link RequestReader} object for the action
     */
    protected abstract RequestReader<RequestT, ActionRequestT> requestReader(RequestT request, Context context);

    /**
     * Returns a {@link ResponseWriter} object for transforming a action result object to
     * a response from Lambda function.
     *
     * @param request The incoming request object
     * @param context The AWS Lambda context
     * @return A {@link ResponseWriter} object for writing a response from the Lambda function
     */
    protected abstract ResponseWriter<ActionResultT, ResponseT> responseWriter(RequestT request, Context context);

}
