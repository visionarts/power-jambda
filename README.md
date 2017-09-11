# Power-Jambda

[![GitHub license](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/visionarts/power-jambda/master/LICENSE)
[![Build Status](https://travis-ci.org/visionarts/power-jambda.svg?branch=master)](https://travis-ci.org/visionarts/power-jambda)
[![Coverage Status](https://codecov.io/gh/visionarts/power-jambda/branch/master/graph/badge.svg)](https://codecov.io/gh/visionarts/power-jambda)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.visionarts/power-jambda-pom/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.visionarts/power-jambda-pom)
[![Javadocs](http://javadoc.io/badge/com.visionarts/power-jambda-core.svg)](http://javadoc.io/doc/com.visionarts/power-jambda-core)

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
    <version>0.9.2</version>
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

See [RESTful API example](samples/simple-case) for more information.

### Tutorial: Return RESTful API response

TBD

### Tutorial: Error Handling

TBD

### Tutorial: Customizing the HTTP Response

TBD

### Tutorial: CORS support
By default, injects the ``Access-Control-Allow-Origin: *`` header to your responses,
including error responses return from action.

If more control of the CORS headers is desired, set the ``cors``
parameter in your ``@Route`` to an instance of ``CorsConfiguration``.
The ``CorsConfiguration`` interface can be implemented as follows.

```Java
public class MyCorsConfig implements CorsConfiguration {
    @Override
    public Optional<String> getAllowOrigin() {
        return Optional.of("http://www.example.com");
    }

    @Override
    public Optional<Boolean> getAllowCredentials() {
        return Optional.of(true);
    }
}
```
```Java
@Route(resourcePath = "/sample", methods = HttpMethod.POST, cors = MyCorsConfig.class)
public class SampleAction extends AbstractLambdaAction<SampleMessageBody, SampleMessageBody> {
}
```
If you disable CORS support, specify ``cors = NoneCorsConfiguration.class`` in your ``@Route``.

    
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

