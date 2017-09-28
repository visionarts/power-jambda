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

package com.visionarts.powerjambda.events.dynamodbv2;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Function;

import mockit.Deencapsulation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.actions.TestAction1;
import com.visionarts.powerjambda.events.AwsEventRequest;
import com.visionarts.powerjambda.events.EventConstants.DynamoDbEventName;
import com.visionarts.powerjambda.events.dynamodb.DynamoDbEventResult;
import com.visionarts.powerjambda.events.model.DynamoDbEvent;
import com.visionarts.powerjambda.events.model.DynamoDbEvent.DynamoDbStreamRecord;
import com.visionarts.powerjambda.testing.MockLambdaContext;


/**
 * Test case for @{link DynamoDbV2EventHandler} class. <br>
 * <br>
 */
public class DynamoDbV2EventHandlerTest {

    private static final String REQUEST_JSON_TEMPLATE = "events/dynamodbv2.json";
    private static final ObjectMapper om = new ObjectMapper();
    private static final Context mockContext = new MockLambdaContext();

    private DynamoDbV2EventHandler handler;
    private Function<DynamoDbStreamRecord, ?> mapper;
    private InputStream input;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @SuppressWarnings("unused")
    private static class TestEventBody {
        public String a;
        public String b;
        public Integer c;

        public TestEventBody() {}
        public TestEventBody(String a, String b, Integer c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o, false);
        }
    }

    @Before
    public void setUp() throws Exception {
        mapper = record -> {
            TestEventBody model = new TestEventBody();
            model.a = record.getDynamodb().getNewImage().get("A").getS();
            model.b = record.getDynamodb().getNewImage().get("B").getS();
            model.c = Integer.valueOf(record.getDynamodb().getNewImage().get("C").getN());
            return model;
        };
        handler = new DynamoDbV2EventHandler(new ApplicationContext(this.getClass()), 1,
                mapper,
                DynamoDbEventName.INSERT, DynamoDbEventName.MODIFY);
        input = this.getClass().getClassLoader().getResourceAsStream(REQUEST_JSON_TEMPLATE);
    }

    @Test
    public void testDynamoDbv2EventHandlerSuccessfully() throws Exception {
        DynamoDbEventResult result = handler.handleRequest(supplyEvent(input), mockContext);

        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

    @Test
    public void testReadDynamoDbStreamRecord() throws Exception {
        AwsEventRequest req = Deencapsulation.invoke(handler, "readDynamoDbStreamRecord", supplyEvent(input).getRecords().get(0));

        assertEquals(TestAction1.class.getName(), req.getAction());
        assertEquals(new TestEventBody("A", "B", 0), om.readValue(req.getBody(), TestEventBody.class));
        assertTrue(req.getAttributes().isEmpty());
    }

    private DynamoDbEvent supplyEvent(InputStream input) {
        try {
            return om.readValue(input, DynamoDbEvent.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
