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

package com.visionarts.powerjambda.logging;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.AwsProxyRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Helper class that operates for the logger on lambda framework.
 *
 */
public class LambdaLoggerHelper {

    public static final String LOG_LEVEL_ENV_NAME = "LOG_LEVEL";
    public static final Level DEFAULT_LOG_LEVEL = Level.INFO;

    private static final List<String> DEFAULT_LOGGING_HEADERS = Arrays.asList(
            "X-Amz-Cf-Id",
            "X-Amzn-Trace-Id");

    private static List<String> availableLoggingHeaders;

    // Suppresses default constructor, ensuring non-instantiability.
    private LambdaLoggerHelper() {
    }

    public static void initialize(Context context) {
        initialize(context, DEFAULT_LOGGING_HEADERS);
    }

    public static void initialize(Context context, List<String> loggingHeaders) {
        availableLoggingHeaders = Objects.requireNonNull(loggingHeaders);
        updateLevel(getLogLevel(DEFAULT_LOG_LEVEL));
        ThreadContext.put("requestId", context.getAwsRequestId());
        ThreadContext.put("version", context.getFunctionVersion());
    }

    public static void clear() {
        ThreadContext.clearAll();
    }

    public static void setRequestInfo(AwsProxyRequest request) {
        Optional.ofNullable(request.getRequestContext())
            .map(c -> c.getIdentity())
            .map(i -> i.getSourceIp())
            .ifPresent(ip -> ThreadContext.put("sourceIp", ip));
        Optional.ofNullable(request.getHeaders())
            .ifPresent(hdrs -> {
                availableLoggingHeaders.forEach(
                    lhdr -> putIfNotNullValue(lhdr, hdrs.get(lhdr)));
            });
    }

    private static void putIfNotNullValue(String key, String value) {
        Optional.ofNullable(value).ifPresent(v -> ThreadContext.put(key, v));
    }

    private static void updateLevel(Level level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        Level curLevel = loggerConfig.getLevel();
        if (curLevel != level) {
            loggerConfig.setLevel(level);
            ctx.updateLoggers();
        }
    }

    private static Level getLogLevel(Level defaultLevel) {
        Level level = Optional.ofNullable(System.getenv(LOG_LEVEL_ENV_NAME))
                .map(v -> Level.toLevel(v, defaultLevel))
                .orElse(defaultLevel);
        return level;
    }
}
