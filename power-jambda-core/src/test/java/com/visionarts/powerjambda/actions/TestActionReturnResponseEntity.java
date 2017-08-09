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
package com.visionarts.powerjambda.actions;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.actions.models.Person;
import com.visionarts.powerjambda.annotations.Route;
import com.visionarts.powerjambda.http.HttpMethod;
import com.visionarts.powerjambda.models.ActionRequest;
import com.visionarts.powerjambda.models.ResponseEntity;


/**
 * . <br>
 * <br>
 */
@Route(resourcePath = "/test_return_responseentity", methods = HttpMethod.POST)
public class TestActionReturnResponseEntity extends AbstractLambdaAction<Person, ResponseEntity<Person>> {

    /**
     * {@inheritDoc}
     * @see com.visionarts.powerjambda.LambdaAction#handle(java.lang.Object, com.amazonaws.services.lambda.runtime.Context)
     */
    @Override
    public ResponseEntity<Person> handle(ActionRequest<Person> request, Context context) {
        Person reqPerson = request.getBody();
        // echo
        Person resBody = new Person();
        resBody.name = reqPerson.name;
        resBody.age = reqPerson.age;

        return new ResponseEntity<Person>()
                    .statusCode(201)
                    .body(resBody);
    }

    /**
     * {@inheritDoc}
     * @see com.visionarts.powerjambda.actions.AbstractLambdaAction#actionBodyType()
     */
    @Override
    public Class<Person> actionBodyType() {
        return Person.class;
    }
}
