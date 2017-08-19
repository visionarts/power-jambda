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

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.actions.S3EventAction;
import com.visionarts.powerjambda.events.model.EventActionRequest;
import com.visionarts.powerjambda.testing.MockLambdaContext;

import mockit.Expectations;


/**
 * Test case for {@link S3EventHandler} class. <br>
 * <br>
 */
public class S3EventHandlerTest {

    private static final String S3_REQUEST_JSON_TEMPLATE = "events/s3.json";
    private static final ObjectMapper om = new ObjectMapper();
    private static final Context mockContext = new MockLambdaContext();

    private S3EventHandler handler;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        handler = new S3EventHandler(new ApplicationContext(this.getClass()), S3EventAction.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testS3EventViaSNSEventHandlerSuccessfully() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(S3_REQUEST_JSON_TEMPLATE);

        new Expectations(S3EventAction.class) {
            {
                new S3EventAction().handle((EventActionRequest<S3EventNotification>)any, (Context)any);
                times = 1;
            }
        };

        S3EventResult result = handler.handleRequest(supplyEvent(input), mockContext);
        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

    private S3EventNotification supplyEvent(InputStream input) {
        try {
            return om.readValue(input, S3EventNotification.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
