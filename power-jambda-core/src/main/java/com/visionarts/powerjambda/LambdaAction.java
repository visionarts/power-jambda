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

import com.amazonaws.services.lambda.runtime.Context;


/**
 * The Lambda action implements AWS Lambda Function application logic using plain old java objects(POJOs)
 * as input and output. <br>
 * <br>
 * @param <Request> The type of the request to the action
 * @param <Result> The type of the result from the action
 */
public interface LambdaAction<Request, Result> {

    /**
     * Handles the action request.
     *
     * @param request The action request
     * @param context The Lambda execution environment context object
     * @return The action result as the Lambda Function response
     * @throws Exception
     */
     Result handle(Request request, Context context) throws Exception;

    /**
     * Here you do the job before action if any.
     *
     * <p> Note that the action handler will skip if any exceptions occurs during this method.
     *
     * <p> This method should be overridden by subclasses.
     *
     * @param request The action request
     * @param context The Lambda context passed by the AWS Lambda environment
     * @throws Exception
     */
    default void beforeAction(Request request, Context context) throws Exception {
        // do something as you like
    }

    /**
     * Here you do the job after action if any.
     *
     * <p> This method should be overridden by subclasses.
     *
     * @param request The action request
     * @param context The Lambda context passed by the AWS Lambda environment
     */
    default void afterAction(Request request, Context context) {
        // do something as you like
    }

}
