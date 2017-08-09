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
package com.visionarts.powerjambda.events.s3;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.util.IOUtils;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.actions.S3EventAction;
import com.visionarts.powerjambda.events.model.EventActionRequest;
import com.visionarts.powerjambda.testing.MockLambdaContext;

import mockit.Expectations;


/**
 * Test case for {@link S3EventExecutor} class. <br>
 * <br>
 */
public class S3EventExecutorTest {

    private static final String S3_VIA_SNS_REQUEST_JSON_TEMPLATE = "events/s3_via_sns.json";
    private static final Context mockContext = new MockLambdaContext();

    private S3EventViaSNSExecutor executor;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        executor = new S3EventViaSNSExecutor(S3EventAction.class);
        executor.setApplicationContext(new ApplicationContext(this.getClass()));
    }

    @Test
    public void testResolveS3EventViaSNSEventSuccessfully() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(S3_VIA_SNS_REQUEST_JSON_TEMPLATE);

        Optional<S3EventNotification> event = executor.resolve(readJson(input));
        assertEquals(1, event.get().getRecords().size());
        assertEquals("aws:s3", event.get().getRecords().get(0).getEventSource());
        assertEquals("us-east-1", event.get().getRecords().get(0).getAwsRegion());
        assertEquals("dev-nsmg-logs-temp", event.get().getRecords().get(0).getS3().getBucket().getName());
        assertEquals("transformed_sample_logs.json", event.get().getRecords().get(0).getS3().getObject().getKey());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testS3EventViaSNSEventHandlerSuccessfully() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(S3_VIA_SNS_REQUEST_JSON_TEMPLATE);

        new Expectations(S3EventAction.class) {
            {
                new S3EventAction().handle((EventActionRequest<S3EventNotification>)any, (Context)any);
                times = 1;
            }
        };

        Optional<S3EventResult> result = executor.apply(readJson(input), mockContext);
        assertEquals(1, result.get().getSuccessItems().size());
        assertEquals(0, result.get().getFailureItems().size());
        assertEquals(0, result.get().getSkippedItems().size());
    }

    private byte[] readJson(InputStream jsonStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOUtils.copy(jsonStream, baos);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return baos.toByteArray();
    }

}
