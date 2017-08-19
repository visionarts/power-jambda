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

package com.visionarts.powerjambda.exceptions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.AwsProxyResponse;
import com.visionarts.powerjambda.actions.JsonResponseWriter;
import com.visionarts.powerjambda.actions.TestActionWithExceptionHandler;
import com.visionarts.powerjambda.actions.TestActionWithoutExceptionHandler;
import com.visionarts.powerjambda.actions.TestActionWithExceptionHandler.TestErrorBody;
import com.visionarts.powerjambda.exceptions.DefaultActionExceptionResolver;
import com.visionarts.powerjambda.exceptions.InternalErrorException;
import com.visionarts.powerjambda.testutils.JsonUtils;

import mockit.Mocked;


/**
 * Test case for {@link DefaultActionExceptionResolver} class. <br>
 * <br>
 */
public class DefaultActionExceptionResolverTest {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private DefaultActionExceptionResolver<AwsProxyResponse> resolver;

    @Mocked
    private Context mockContext;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        resolver = new DefaultActionExceptionResolver<>(new JsonResponseWriter<>());
    }

    @Test
    public void invokeUserDefinedExceptionHandlerTest() throws Exception {

        TestActionWithExceptionHandler action = new TestActionWithExceptionHandler();
        AwsProxyResponse res = resolver.handleException(
                new TestActionWithExceptionHandler.HandledException("test exception"),
                action, mockContext);

        assertEquals(503, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        TestErrorBody actual = JsonUtils.readValue(res.getBody(), TestErrorBody.class);
        assertEquals("Test Error test exception", actual.error);
        assertEquals("Test descrption", actual.description);
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void throwsExceptionWhenNotExistsExceptionHandlerTest() throws Exception {

        thrown.expect(InternalErrorException.class);
        thrown.expectMessage(startsWith("Not found any ExceptionHandler for"));

        TestActionWithoutExceptionHandler action = new TestActionWithoutExceptionHandler();
        resolver.handleException(
                new RuntimeException("Occur exception test"),
                action, mockContext);

    }

}
