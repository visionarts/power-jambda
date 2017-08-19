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

package com.visionarts.powerjambda.events.schedule;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.ApplicationContext;
import com.visionarts.powerjambda.actions.TestScheduledAction;
import com.visionarts.powerjambda.events.model.EventActionRequest;
import com.visionarts.powerjambda.events.model.ScheduledEvent;
import com.visionarts.powerjambda.models.EmptyActionBody;
import com.visionarts.powerjambda.testing.MockLambdaContext;

import mockit.Expectations;


/**
 * Test case for {@link ScheduledEventHandler} class. <br>
 * <br>
 */
public class ScheduledEventHandlerTest {

    private static final String REQUEST_JSON_TEMPLATE = "events/scheduled_event.json";
    private static final ObjectMapper om = new ObjectMapper();
    private static final Context mockContext = new MockLambdaContext();

    private ScheduledEvent event;
    private ScheduledEventHandler handler;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        handler = new ScheduledEventHandler(new ApplicationContext(this.getClass()));
        InputStream src = this.getClass().getClassLoader().getResourceAsStream(REQUEST_JSON_TEMPLATE);
        event = om.readValue(src, ScheduledEvent.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testScheduledEventHandlerSuccessfully() throws Exception {

        new Expectations(TestScheduledAction.class) {
            {
                new TestScheduledAction().handle((EventActionRequest<EmptyActionBody>)any, (Context)any);
                times = 1;
            }
        };

        ScheduledEventResult result = handler.handleRequest(event, mockContext);
        assertEquals(1, result.getSuccessItems().size());
        assertEquals(0, result.getFailureItems().size());
        assertEquals(0, result.getSkippedItems().size());
    }

}
