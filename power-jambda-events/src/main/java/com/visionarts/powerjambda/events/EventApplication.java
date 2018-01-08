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

package com.visionarts.powerjambda.events;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.logging.LambdaLoggerHelper;
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
 * In most circumstances the static
 * {@link #run(Class, EventExecutorRegistry, InputStream, OutputStream, Context)}
 * method can be called directly from your Lambda main handler:
 *
 * <pre class="code">
 * public class MyEventApplication {
 *
 *   private static final EventExecutorRegistry registry = new EventExecutorRegistry()
 *       .register(new DynamoDbEventExecutor())
 *       .register(new SnsEventExecutor())
 *       .register(new ScheduledEventExecutor());
 *
 *   public static void handler(InputStream input, OutputStream output, Context context) throws Exception {
 *       EventApplication.run(MyEventApplication.class, registry, input, output, context);
 *   }
 * </pre>
 */
public final class EventApplication {

    private static final Logger logger = LogManager.getLogger(EventApplication.class);
    private static final String APPLICATION_NAME = EventApplication.class.getSimpleName();

    private static EventApplication application;

    private ApplicationContext applicationContext;
    private LambdaEventHandler handler;

    static {
        initialize();
    }

    private EventApplication(Class<?> mainApplicationClazz, EventExecutorRegistry registry) {
        this.applicationContext = new ApplicationContext(Objects.requireNonNull(mainApplicationClazz));
        this.handler = new LambdaEventHandler(applicationContext, Objects.requireNonNull(registry));
    }

    /**
     * Static helper that can be used to run your LambdaEventApplication using default settings.
     *
     * @param lambdaMainHandlerClazz
     *            The Lambda main handler class
     * @param registry
     *            The EventExecutor registry for handling events
     * @param input
     *            The InputStream for the incoming event
     * @param output
     *            An OutputStream where the response returned by the action class is written
     * @param context
     *            The Lambda Context object passed by the AWS Lambda environment
     *
     * @throws Exception Thrown if an error in processing
     */
    public static void run(Class<?> lambdaMainHandlerClazz, EventExecutorRegistry registry,
                           InputStream input, OutputStream output,
                           Context context) throws Exception {
        run(lambdaMainHandlerClazz, registry, input, output, context, null);
    }

    /**
     * Static helper that can be used to run your LambdaEventApplication with
     * the method to run only once on startup this Lambda function.
     *
     * @param lambdaMainHandlerClazz
     *            The Lambda main handler class
     * @param registry
     *            The EventExecutor registry for handling events
     * @param input
     *            The InputStream for the incoming event
     * @param output
     *            An OutputStream where the response returned by the action class is written
     * @param context
     *            The Lambda Context object passed by the AWS Lambda environment
     * @param startupHandler
     *            A start up handler to invoke when you call a Lambda function for the first time
     *
     * @throws Exception Thrown if an error in processing
     */
    public static void run(Class<?> lambdaMainHandlerClazz, EventExecutorRegistry registry,
                           InputStream input, OutputStream output,
                           Context context, Consumer<ApplicationContext> startupHandler) throws Exception {
        application = Optional.ofNullable(application).orElseGet(
            () -> {
                EventApplication app = new EventApplication(lambdaMainHandlerClazz, registry);
                Optional.ofNullable(startupHandler)
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

    private void handle(InputStream input, OutputStream output, Context context) throws Exception {
        LambdaLoggerHelper.putThreadContext(context);
        try {
            handler.lambdaHandler(input, output, context);
        } finally {
            LambdaLoggerHelper.clear();
        }
    }
}
