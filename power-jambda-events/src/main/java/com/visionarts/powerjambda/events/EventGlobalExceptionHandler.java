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

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.GlobalExceptionHandler;
import com.visionarts.powerjambda.exceptions.ClientErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default exception handler for framework.<br>
 * <br>
 */
public class EventGlobalExceptionHandler implements GlobalExceptionHandler<AwsEventResponse> {

    private static final Logger logger = LogManager.getLogger(EventGlobalExceptionHandler.class);

    public AwsEventResponse handleException(Exception ex, Context context) {
        logger.error("Catching Internal Server Error at default exception handler", ex);
        return new AwsEventResponse(ex);
    }

    public AwsEventResponse handleException(ClientErrorException ex, Context context) {
        logger.error("Catching Client Error at default exception handler", ex);
        return new AwsEventResponse(ex);
    }

}
