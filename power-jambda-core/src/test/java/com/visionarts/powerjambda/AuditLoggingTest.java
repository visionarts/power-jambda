package com.visionarts.powerjambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.actions.models.ComplexData;
import com.visionarts.powerjambda.actions.models.Person;
import com.visionarts.powerjambda.testing.AwsProxyRequestBuilder;
import com.visionarts.powerjambda.testing.MockLambdaContext;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.layout.JsonLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;


public class AuditLoggingTest {

    private static final String REQUEST_JSON_TEMPLATE = "requests/awsproxyrequest_api-gateway.json";
    private static final Context mockContext = new MockLambdaContext();
    private static final String LOGGER_NAME = "audit-logger";

    private ActionRouter router;
    private InputStream requestJsonStream;
    private StringWriter writer;

    @Before
    public void setUp() throws Exception {
        router = new ActionRouter(new ApplicationContext(this.getClass()));
        requestJsonStream = this.getClass().getClassLoader().getResourceAsStream(REQUEST_JSON_TEMPLATE);

        writer = new StringWriter();
        Appender appender = WriterAppender.createAppender(
                JsonLayout.createDefaultLayout(), null, writer, LOGGER_NAME, false, true);
        LoggerContext.getContext(false).getConfiguration()
                .getRootLogger().addAppender(appender, null, null);
    }

    @After
    public void tearDown() {
        LoggerContext.getContext(false).getConfiguration()
                .getRootLogger().removeAppender(LOGGER_NAME);
    }

    @Test
    public void testAuditLog() throws Exception {
        //Given
        ComplexData body = new ComplexData() {{
            setPerson(new Person() {{
                name = "bar";
                age = 10;
            }});
        }};

        //When
        AwsProxyRequest req = new AwsProxyRequestBuilder(requestJsonStream)
                .resourcePath("/test_action3")
                .httpMethod("POST")
                .body(body)
                .build();

        AwsProxyResponse res = router.apply(req, mockContext);

        //Then
        assertEquals(200, res.getStatusCode());

        List<String> messages = Arrays.stream(writer.toString().split("\n"))
                .filter(s -> s.contains("\"message\""))
                .collect(Collectors.toList());

        assertThat(messages.get(0), containsString(
                "Starting com.visionarts.powerjambda.actions.TestAction3 " +
                        "method: POST path: /path/to/resource params: {proxy=path/to/resource} " +
                        "body: com.visionarts.powerjambda.actions.models.ComplexData " +
                        "({\\\"count\\\":1,\\\"persons\\\":[{\\\"name\\\":\\\"bar\\\",\\\"age\\\":10}]})"));
        assertThat(messages.get(1), containsString(
                "Returned action result: " +
                        "{\\\"persons\\\":[{\\\"name\\\":\\\"bar\\\",\\\"age\\\":10}," +
                        "{\\\"name\\\":\\\"BAR\\\",\\\"age\\\":20}],\\\"count\\\":2}"));
        assertThat(messages.get(2), containsString(
                "End com.visionarts.powerjambda.actions.TestAction3"));
        assertThat(messages.get(3), containsString("Response status code: 200"));
    }
}
