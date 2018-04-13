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

import java.io.InputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.actions.TestActionWithExceptionHandler.TestErrorBody;
import com.visionarts.powerjambda.actions.models.ComplexData;
import com.visionarts.powerjambda.actions.models.Person;
import com.visionarts.powerjambda.models.DefaultErrorResponseBody;
import com.visionarts.powerjambda.testing.AwsProxyRequestBuilder;
import com.visionarts.powerjambda.testing.MockLambdaContext;
import com.visionarts.powerjambda.testutils.JsonUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Test case for Router class. <br>
 * <br>
 */
public class ActionRouterTest {

    private static final String REQUEST_JSON_TEMPLATE = "requests/awsproxyrequest_api-gateway.json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json; charset=utf-8";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final Context mockContext = new MockLambdaContext();

    private ActionRouter router;
    private InputStream requestJsonStream;

    @Before
    public void setUp() throws Exception {
        router = new ActionRouter(new ApplicationContext(this.getClass()));
        requestJsonStream = this.getClass().getClassLoader().getResourceAsStream(REQUEST_JSON_TEMPLATE);
    }

    @Test
    public void testRouter1() throws Exception {

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action1")
                .httpMethod("GET")
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(200, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertEquals("\"TestAction1\"", res.getBody());
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testRouter2() throws Exception {

        Person expect = new Person();
        expect.name = "foo";
        expect.age = 10;

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action2")
                .httpMethod("POST")
                .body(expect)
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(200, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertThat(JsonUtils.readValue(res.getBody(), Person.class), equalTo(expect));
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testRouter3() throws Exception {

        Person person = new Person();
        person.name = "bar";
        person.age = 10;
        ComplexData body = new ComplexData();
        body.setPerson(person);

        Person addedPerson = new Person();
        addedPerson.name = person.name.toUpperCase();
        addedPerson.age = person.age + 10;

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action3")
                .httpMethod("POST")
                .body(body)
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(200, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        ComplexData actual = JsonUtils.readValue(res.getBody(), ComplexData.class);
        assertThat(actual.persons, contains(person, addedPerson));
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testRouterNotFoundRoute() throws Exception {

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/not_found_route")
                .httpMethod("GET")
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(404, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertEquals("{\"error\":\"Client Error\",\"description\":\"Action not found\"}", res.getBody());
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testRouterReturnResponseEntity() throws Exception {

        Person expect = new Person();
        expect.name = "foo";
        expect.age = 10;

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_return_responseentity")
                .httpMethod("POST")
                .body(expect)
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(201, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertThat(JsonUtils.readValue(res.getBody(), Person.class), equalTo(expect));
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testHandleUserDefinedException() throws Exception {

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action_with_exceptionhandler")
                .httpMethod("GET")
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(503, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        TestErrorBody actual = JsonUtils.readValue(res.getBody(), TestErrorBody.class);
        assertEquals("Test Error Occur exception", actual.error);
        assertEquals("Test descrption", actual.description);
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testHandleUserNotDefinedException() throws Exception {

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action_without_exceptionhandler")
                .httpMethod("GET")
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(500, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        DefaultErrorResponseBody actual = JsonUtils.readValue(res.getBody(), DefaultErrorResponseBody.class);
        assertEquals("Internal Server Error", actual.error);
        assertEquals("Not found any ExceptionHandler for java.lang.RuntimeException", actual.description);
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testRouterActionRequestReadRequestError() throws Exception {

        // this request body occurs deserialize error
        String errorBody = "{\"aaa\": }";

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action2")
                .httpMethod("POST")
                .body(errorBody)
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(400, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertEquals(
                "{\"error\":\"Client Error\",\"description\":\"Unexpected exception during reading a request\"}",
                res.getBody());
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testSecureActionSuccessfully() throws Exception {

        String cred = "OkCredential";

        Person expect = new Person();
        expect.name = cred;
        expect.age = 10;

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_secure_action")
                .httpMethod("GET")
                .header("Authorization", cred)
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(200, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertThat(JsonUtils.readValue(res.getBody(), Person.class), equalTo(expect));
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testSecureActionBadCredential() throws Exception {

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_secure_action")
                .httpMethod("GET")
                .header("Authorization", "NgCredential")
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(401, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertThat(res.getHeaders(), hasEntry(ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
        assertEquals(
                "{\"error\":\"Unauthorized Error\",\"description\":\"Bad Credentials\"}",
                res.getBody());
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testActionWithoutCors() throws Exception {

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action_without_cors")
                .httpMethod("GET")
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(200, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertThat(res.getHeaders(), is(not((hasKey(ACCESS_CONTROL_ALLOW_ORIGIN)))));
        assertThat(res.getHeaders(), is(not((hasKey(ACCESS_CONTROL_ALLOW_CREDENTIALS)))));
        assertEquals("\"TestActionWithoutCors\"", res.getBody());
        assertFalse(res.isBase64Encoded());
    }

    @Test
    public void testActionWithMyCors() throws Exception {

        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action_with_mycors")
                .httpMethod("GET")
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        assertEquals(200, res.getStatusCode());
        assertThat(res.getHeaders(), hasEntry(CONTENT_TYPE, APPLICATION_JSON));
        assertThat(res.getHeaders(), hasEntry(ACCESS_CONTROL_ALLOW_ORIGIN, "http://www.example.com"));
        assertThat(res.getHeaders(), hasEntry(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
        assertEquals("\"TestActionWithMyCors\"", res.getBody());
        assertFalse(res.isBase64Encoded());
    }
}
