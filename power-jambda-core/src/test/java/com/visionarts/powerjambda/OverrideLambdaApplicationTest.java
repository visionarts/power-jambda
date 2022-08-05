package com.visionarts.powerjambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.actions.TestAction3;
import com.visionarts.powerjambda.actions.models.ComplexData;
import com.visionarts.powerjambda.actions.models.Person;
import com.visionarts.powerjambda.testing.AwsProxyRequestBuilder;
import com.visionarts.powerjambda.testing.MockLambdaContext;
import com.visionarts.powerjambda.testutils.JsonUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;


public class OverrideLambdaApplicationTest {

    private static final String REQUEST_JSON_TEMPLATE = "requests/awsproxyrequest_api-gateway.json";
    private static final Context mockContext = new MockLambdaContext();

    private InputStream requestJsonStream;
    private OutputStream output;

    @Before
    public void setUp() throws Exception {
        requestJsonStream = this.getClass().getClassLoader().getResourceAsStream(REQUEST_JSON_TEMPLATE);
        output = new ByteArrayOutputStream();
    }

    @Test
    public void requestTestAction2ButRouteToTestAction3() throws Exception {
        //Given
        ComplexData expect = new ComplexData();
        Person person = new Person();
        expect.setPerson(person);
        person.name = "foo";
        person.age = 10;

        //When
        InputStream input = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action2")
                .httpMethod("POST")
                .body(expect)
                .buildAsStream();

        ExtendedLambdaApplication.run(input, output, mockContext);

        //Then
        AwsProxyResponse res = JsonUtils.parseAwsProxyResponse(output);
        assertEquals(200, res.getStatusCode());

        // Requested "/test_action2" but `findAction` in ExtendedLambdaApplication returns TestAction3.class.
        assertThat(JsonUtils.readValue(res.getBody(), ComplexData.class), instanceOf(ComplexData.class));
    }

    private static class ExtendedLambdaApplication extends LambdaApplication {

        public static void run(InputStream input, OutputStream output, Context context) throws Exception {
            new ExtendedLambdaApplication().handle(input, output, context);
        }

        @Override
        protected void handle(InputStream input, OutputStream output, Context context) throws Exception {

            AwsProxyResponse response = new ActionRouter(this.applicationContext) {
                @Override
                protected Optional<Class<?>> findAction(String packagePath, AwsProxyRequest request) {
                    return Optional.of(TestAction3.class);
                }
            }.apply(parseRequest(input), context);

            writeResponse(response, output);
        }
    }
}
