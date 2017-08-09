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

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.testing.MockLambdaContext;


/**
 * Test case for DynamodbEventHandler class. <br>
 * <br>
 */
public class EventApplicationTest {

    private static final String DYNAMO_REQUEST_JSON_TEMPLATE = "events/dynamodb.json";
    private static final String SNS_REQUEST_JSON_TEMPLATE = "events/sns.json";
    private static final String S3_VIA_SNS_REQUEST_JSON_TEMPLATE = "events/s3_via_sns.json";
    private static final String S3_REQUEST_JSON_TEMPLATE = "events/s3.json";
    private static final String SCHEDULED_EVENT_JSON_TEMPLATE = "events/scheduled_event.json";
    private static final ObjectMapper om = new ObjectMapper();
    private static final Context mockContext = new MockLambdaContext();

    private InputStream dynamoEvent;
    private InputStream snsEvent;
    private InputStream s3viaSnsEvent;
    private InputStream s3Event;
    private InputStream scheduledEvent;
    private ByteArrayOutputStream response;

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Before
    public void setUp() throws Exception {

        dynamoEvent = this.getClass().getClassLoader().getResourceAsStream(DYNAMO_REQUEST_JSON_TEMPLATE);
        snsEvent = this.getClass().getClassLoader().getResourceAsStream(SNS_REQUEST_JSON_TEMPLATE);
        s3viaSnsEvent = this.getClass().getClassLoader().getResourceAsStream(S3_VIA_SNS_REQUEST_JSON_TEMPLATE);
        s3Event = this.getClass().getClassLoader().getResourceAsStream(S3_REQUEST_JSON_TEMPLATE);
        scheduledEvent = this.getClass().getClassLoader().getResourceAsStream(SCHEDULED_EVENT_JSON_TEMPLATE);
        response = new ByteArrayOutputStream();
    }

    @Test
    public void testDynamodbEventHandlerSuccessfully() throws Exception {

        TestEventAppMain.lambdaHandler(dynamoEvent, response, mockContext);

        Result<?> result = readResult(response);
        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

    @Test
    public void testSNSEventHandlerSuccessfully() throws Exception {

        TestEventAppMain.lambdaHandler(snsEvent, response, mockContext);

        Result<?> result = readResult(response);
        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

    @Test
    public void testS3viaSNSEventHandlerSuccessfully() throws Exception {

        TestEventAppMain.lambdaHandler(s3viaSnsEvent, response, mockContext);

        Result<?> result = readResult(response);
        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

    @Test
    public void testS3EventHandlerSuccessfully() throws Exception {

        TestEventAppMain.lambdaHandler(s3Event, response, mockContext);

        Result<?> result = readResult(response);
        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

    @Test
    public void testScheduledEventHandlerSuccessfully() throws Exception {

        TestEventAppMain.lambdaHandler(scheduledEvent, response, mockContext);

        Result<?> result = readResult(response);
        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

    private Result<?> readResult(ByteArrayOutputStream result) {
        try {
            return om.readValue(result.toByteArray(), Result.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
