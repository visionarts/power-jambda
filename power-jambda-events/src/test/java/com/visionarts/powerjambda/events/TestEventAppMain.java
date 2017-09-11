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

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.events.actions.S3EventAction;
import com.visionarts.powerjambda.events.dynamodb.DynamoDbEventExecutor;
import com.visionarts.powerjambda.events.s3.S3EventExecutor;
import com.visionarts.powerjambda.events.s3.S3EventViaSnsExecutor;
import com.visionarts.powerjambda.events.schedule.ScheduledEventExecutor;
import com.visionarts.powerjambda.events.sns.SnsEventExecutor;

public class TestEventAppMain {
    private static final EventExecutorRegistry registry = new EventExecutorRegistry()
        .register(new DynamoDbEventExecutor())
        .register(new S3EventViaSnsExecutor(S3EventAction.class)
                    .throwOnFailure(true))
        .register(new SnsEventExecutor()
                    .throwOnFailure(true))
        .register(new S3EventExecutor(S3EventAction.class)
                    .throwOnFailure(true))
        .register(new ScheduledEventExecutor()
                    .throwOnFailure(true));

    /**
     * Test for the main Lambda function handler for an event.<br>
     * <br>
     * @throws Exception
     */
    public static void lambdaHandler(InputStream input, OutputStream output, Context context) throws Exception {
        EventApplication.run(TestEventAppMain.class, registry, input, output,
                context, applicationContext -> { /* dummy */ });
    }
}
