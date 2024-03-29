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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.exceptions.ClientErrorException;
import com.visionarts.powerjambda.exceptions.InternalErrorException;
import com.visionarts.powerjambda.logging.LambdaLoggerHelper;
import com.visionarts.powerjambda.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class that can be used to launch a Lambda application from
 * the main Lambda function handler.<br>
 *
 * By default class will perform the following steps to bootstrap your
 * application: <br>
 *
 * <br>
 * In most circumstances the static {@link #run(Class, InputStream, OutputStream, Context)}
 * method can be called directly from your Lambda main handler:
 *
 * <pre class="code">
 * public class MyLambdaApplication {
 *
 * public static void handler(InputStream input, OutputStream output, Context context) throws Exception {
 *     LambdaApplication.run(MyLambdaApplication.class, input, output, context);
 * }
 * </pre>
 */
public class LambdaApplication {

    protected static final Logger logger = LogManager.getLogger(LambdaApplication.class);
    protected static final String APPLICATION_NAME = LambdaApplication.class.getSimpleName();

    protected static LambdaApplication application;

    protected ApplicationContext applicationContext;
    protected ActionRouter router;
    protected ObjectMapper om;

    static {
        initialize();
    }

    protected LambdaApplication() {
        this(LambdaApplication.class);
    }

    private LambdaApplication(Class<?> mainApplicationClazz) {
        this.applicationContext = new ApplicationContext(Objects.requireNonNull(mainApplicationClazz));
        this.router = new ActionRouter(this.applicationContext);
        this.om = Utils.getObjectMapper();
    }

    /**
     * Static helper that can be used to run a LambdaApplication using default settings.
     *
     * @param lambdaMainHandlerClazz
     *            The Lambda main handler class
     * @param input
     *            The InputStream for the incoming event
     * @param output
     *            An OutputStream where the response returned by the action class is written
     * @param context
     *            The Lambda Context object passed by the AWS Lambda environment
     *
     * @throws Exception Thrown if an unknown error in processing
     */
    public static void run(Class<?> lambdaMainHandlerClazz,
                           InputStream input, OutputStream output,
                           Context context) throws Exception {
        run(lambdaMainHandlerClazz, input, output, context, null);
    }

    /**
     * Static helper that can be used to run a LambdaApplication with startup handler.
     *
     * @param lambdaMainHandlerClazz
     *            The Lambda main handler class
     * @param input
     *            The InputStream for the incoming event
     * @param output
     *            An OutputStream where the response returned by the action class is written
     * @param context
     *            The Lambda Context object passed by the AWS Lambda environment
     * @param startUpHandler
     *            A start up handler to invoke when you call a Lambda function for the first time
     *
     * @throws Exception Thrown if an unknown error in processing
     */
    public static void run(Class<?> lambdaMainHandlerClazz,
                           InputStream input, OutputStream output,
                           Context context, Consumer<ApplicationContext> startUpHandler) throws Exception {
        application = Optional.ofNullable(application).orElseGet(
            () -> {
                LambdaApplication app = new LambdaApplication(lambdaMainHandlerClazz);
                Optional.ofNullable(startUpHandler)
                    .ifPresent(h -> h.accept(app.applicationContext));
                logger.debug("{} initialized", APPLICATION_NAME);
                return app;
            });
        application.handle(input, output, context);
    }

    private static void initialize() {
        logger.debug("{} initializing", APPLICATION_NAME);
        application = null;
    }

    public void handle(InputStream input, OutputStream output, Context context) throws Exception {
        LambdaLoggerHelper.putThreadContext(context);
        logger.debug("Starting request handler: requestId: {}", context.getAwsRequestId());
        try {
            AwsProxyRequest request = parseRequest(input);
            LambdaLoggerHelper.putThreadContext(request);
            AwsProxyResponse response = router.apply(request, context);
            writeResponse(response, output);
        } finally {
            logger.debug("End request handler: requestId: {}", context.getAwsRequestId());
            LambdaLoggerHelper.clear();
        }
    }

    protected AwsProxyRequest parseRequest(InputStream input)
            throws ClientErrorException, InternalErrorException {
        try {
            AwsProxyRequest request = om.readValue(input, AwsProxyRequest.class);
            logger.debug("Parsed request: {}", () -> Utils.writeValueAsString(request));
            return request;
        } catch (JsonParseException | JsonMappingException e) {
            throw new ClientErrorException("Error while parsing raw request", e);
        } catch (IOException e) {
            throw new InternalErrorException("Error while reading raw request", e);
        }
    }

    protected void writeResponse(AwsProxyResponse response, OutputStream output) {
        logger.debug("Writing response: {}", () -> Utils.writeValueAsString(response));
        try {
            om.writeValue(output, response);
        } catch (IOException e) { // never happen
            throw new UncheckedIOException(e);
        }
    }
}
