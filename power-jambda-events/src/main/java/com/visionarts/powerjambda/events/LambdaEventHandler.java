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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.utils.IOUtils;
import com.visionarts.powerjambda.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class has the main event handler.
 *
 */
public final class LambdaEventHandler {

    private static final Logger logger = LogManager.getLogger(LambdaEventHandler.class);

    private final List<AbstractEventExecutor<?, ?>> executors;

    public LambdaEventHandler(ApplicationContext appContext, EventExecutorRegistry registry) {
        executors = registry.stream()
            .peek(e -> e.setApplicationContext(appContext))
            .collect(Collectors.toList());
    }

    /**
     * The main Lambda function handler for an event.<br>
     * <br>
     */
    public void lambdaHandler(InputStream input, OutputStream output, Context context) {

        logger.info("Start to process the event");

        byte[] inputBytes;
        try {
            inputBytes = IOUtils.toByteArray(input, true);
        } catch (IOException e) { // never happen
            throw logger.throwing(new UncheckedIOException(e));
        }

        logger.debug("Incoming event parameter: {}", () -> new String(inputBytes));

        Optional<? extends Result<?>> result = executors.stream()
            .map(executor -> executor.apply(inputBytes, context))
            .filter(Optional::isPresent)
            .findFirst()
            .get();

        result.ifPresent(r -> {
            logger.info("Event summary: {}", r.summary());
            writeValueQuietly(output, r);
        });

        logger.info("Process finished");
        result.ifPresent(r -> r.throwOnFailure(() -> new RuntimeException("Some event requests failed")));
    }

    private void writeValueQuietly(OutputStream out, Object value) {
        try {
            Utils.getObjectMapper().writeValue(out, value);
            logger.debug("Event result: {}", value);
        } catch (IOException ex) {
            logger.warn("failed to serialize successful event result for logging", ex);
        }
    }
}
