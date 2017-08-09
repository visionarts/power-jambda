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
package com.visionarts.powerjambda.events.dynamodb;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.events.model.DynamodbEventEx;
import com.visionarts.powerjambda.testing.MockLambdaContext;


/**
 * Test case for @{link DynamodbEventHandler} class. <br>
 * <br>
 */
public class DynamodbEventHandlerTest {

    private static final String REQUEST_JSON_TEMPLATE = "events/dynamodb.json";
    private static final String DYNAMO_REQUEST_MULTI_JSON_TEMPLATE = "events/dynamodb_multi_events.json";
    private static final ObjectMapper om = new ObjectMapper();
    private static final Context mockContext = new MockLambdaContext();

    private DynamodbEventHandler handler;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        handler = new DynamodbEventHandler(new ApplicationContext(this.getClass()));
    }

    @Test
    public void testDynamodbEventHandlerSuccessfully() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(REQUEST_JSON_TEMPLATE);
        DynamodbEventResult result = handler.handleRequest(supplyEvent(input), mockContext);
        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

    @Test()
    public void testDynamodbEventHandlerMultiEvent() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(DYNAMO_REQUEST_MULTI_JSON_TEMPLATE);
        DynamodbEventResult result = handler.handleRequest(supplyEvent(input), mockContext);
        assertEquals(2, result.getSuccessItems().size());
        assertEquals(1, result.getFailureItems().size());
        assertEquals(1, result.getSkippedItems().size());
    }

    private DynamodbEventEx supplyEvent(InputStream input) {
        try {
            return om.readValue(input, DynamodbEventEx.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
