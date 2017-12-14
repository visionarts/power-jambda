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

package com.visionarts.powerjambda.testing;

import com.visionarts.powerjambda.AwsProxyRequest;
import com.visionarts.powerjambda.models.ActionRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test case for {@link ActionRequestBuilder} class. <br>
 *
 */
public class ActionRequestBuilderTest {

    private static class TestBody {
        public String value = "test-body";

    }

    @Test
    public void testBuilder() {
        TestBody testBody = new TestBody();
        ActionRequest<TestBody> request =
                new ActionRequestBuilder<TestBody>()
                        .method("GET")
                        .header("x-header", "test-header-value")
                        .path("test-path")
                        .pathParameter("test-param", "test-param-value")
                        .queryStringParameter("test-query", "test-query-value")
                        .stage("test-stage")
                        .stageVariable("test-stage-var", "test-stage-value")
                        .body(testBody)
                        .build();

        assertEquals("GET", request.getMethod());
        assertEquals("test-header-value", request.getHeader("x-header"));
        assertEquals(1, request.getHeaders().size());
        assertEquals("test-path", request.getPath());
        assertEquals("test-param-value", request.getPathParameter("test-param"));
        assertEquals(1, request.getPathParameters().size());
        assertEquals("test-query-value", request.getQueryParameter("test-query"));
        assertEquals(1, request.getQueryParameters().size());
        assertEquals("test-stage", request.getStage());
        assertEquals("test-stage-value", request.getStageVariable("test-stage-var"));
        assertEquals(1, request.getStageVariables().size());
        assertEquals("test-body", request.getBody().value);
    }

    @Test
    public void testBodyAsString() {
        ActionRequest<TestBody> request =
                new ActionRequestBuilder<TestBody>()
                        .bodyAsString("{\"value\":\"test-body-str\"}", TestBody.class)
                        .build();

        assertEquals("{\"value\":\"test-body-str\"}", request.getBodyAsString());
        assertEquals("test-body-str", request.getBody().value);
    }

    @Test
    public void testOriginalRequestNotNull() {
        ActionRequest<TestBody> request =
                new ActionRequestBuilder<TestBody>(new AwsProxyRequest())
                        .build();

        assertNotNull(request.getOriginalRequest());
    }
}
