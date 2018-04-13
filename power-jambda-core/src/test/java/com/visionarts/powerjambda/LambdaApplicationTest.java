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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.actions.models.Person;
import com.visionarts.powerjambda.testing.AwsProxyRequestBuilder;
import com.visionarts.powerjambda.testing.MockLambdaContext;
import com.visionarts.powerjambda.testutils.JsonUtils;
import mockit.Deencapsulation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Test case for {@link LambdaApplication} class. <br>
 * <br>
 *
 */
public class LambdaApplicationTest {

    private static final String REQUEST_JSON_TEMPLATE = "requests/awsproxyrequest_api-gateway.json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json; charset=utf-8";
    private static Context mockContext = new MockLambdaContext();

    private InputStream requestJsonStream;
    private OutputStream output;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        requestJsonStream = this.getClass().getClassLoader().getResourceAsStream(REQUEST_JSON_TEMPLATE);
        output = new ByteArrayOutputStream();
    }

    @Test
    public void testLambdaHandler() throws Exception {

        Person expect = new Person();
        expect.name = "foo";
        expect.age = 10;

        InputStream input = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action2")
                .httpMethod("POST")
                .body(expect)
                .buildAsStream();

        TestLambdaApplication.lambdaHandler(input, output, mockContext);

        AwsProxyResponse res = JsonUtils.parseAwsProxyResponse(output);
        assertEquals(200, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertThat(JsonUtils.readValue(res.getBody(), Person.class), equalTo(expect));
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testLambdaHandlerWithStartupHandler() throws Exception {

        Person expect = new Person();
        expect.name = "foo";
        expect.age = 10;

        Map<String, ApplicationContext> map = new HashMap<>();

        InputStream input = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action2")
                .httpMethod("POST")
                .body(expect)
                .buildAsStream();

        Deencapsulation.invoke(LambdaApplication.class, "initialize");

        TestLambdaApplication.lambdaHandler(input, output, mockContext,
                appContext -> map.put("startupHandler", appContext));

        AwsProxyResponse res = JsonUtils.parseAwsProxyResponse(output);
        assertEquals(200, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertThat(JsonUtils.readValue(res.getBody(), Person.class), equalTo(expect));
        assertFalse(res.isBase64Encoded());
        assertEquals(TestLambdaApplication.class, map.get("startupHandler").getApplicationMainClass());
    }

}
