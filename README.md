# Power-Jambda
The java serverless microframework for RESTful API allows you to quickly develop applications that use Amazon API Gateway and AWS Lambda.

## Getting Started

#### Maven installation ####
The recommended way to get started using this framework
in your project is with a dependency management system

```xml
<dependencies>
  <dependency>
    <groupId>com.visionarts</groupId>
    <artifactId>power-jambda-core</artifactId>
    <version>0.9.0</version>
  </dependency>
</dependencies>
```

#### Quick Start ####
Here you create an Application class to have main Lambda function handler:

src/main/java/sample/SampleApplication.java

```Java
/**
 * Sample application.
 */
public class SampleApplication implements RequestStreamHandler {

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
            LambdaApplication.run(SampleApplication.class, input, output, context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

Now you can create a action class for handling RESTful API request:

src/main/java/sample/action/SampleAction.java
```Java
/**
 * Sample action class.
 */
@Route(resourcePath = "/sample", methods = HttpMethod.POST)
public class SampleAction extends AbstractLambdaAction<SampleMessageBody, SampleMessageBody> {

    @Override
    public Class<SampleMessageBody> actionBodyType() {
        return SampleMessageBody.class;
    }

    @Override
    public SampleMessageBody handle(ActionRequest<SampleMessageBody> request, Context context) {
        // echo back
        SampleMessageBody res = new SampleMessageBody();
        res.message = request.getBody().message;
        res.name = request.getBody().name;
        return res;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SampleErrorModel> handleException(IllegalArgumentException e, Context context) {
        SampleErrorModel body = new SampleErrorModel();
        body.error = "BadRequest";
        body.code = 400001;
        body.description = e.getMessage();

        return new ResponseEntity<SampleErrorModel>()
                .body(body)
                .statusCode(400);
    }

}
```
The class is annotated a **@Route**, meaning it’s ready for use by AWS Lambda to handle  requests. **@Route** maps 'POST /sample' to this action class.
When invoked from API Gateway using curl on the command line, the handle method returns 200 OK, including a json content serialized SampleMessageBody('application/json').

### Tutorial: Return RESTful API response

TBD

### Tutorial: Error Handling

TBD

### Tutorial: Customizing the HTTP Response

TBD

## Building From Source

Once you check out the code from GitHub, you can build it using Maven.

```sh
./mvnw clean install
```

License
-------
Copyright (C) 2017 [Visionarts, Inc](https://www.visionarts.co.jp/)

Distributed under the Apache License v2.0.  See the file LICENSE.


Development and Contribution
----------------------------
We will open for contributions.

