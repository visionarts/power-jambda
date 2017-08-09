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

import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.visionarts.powerjambda.actions.models.Person;
import com.visionarts.powerjambda.annotations.Route;
import com.visionarts.powerjambda.exceptions.UnauthorizedException;
import com.visionarts.powerjambda.http.HttpMethod;
import com.visionarts.powerjambda.models.ActionRequest;
import com.visionarts.powerjambda.models.EmptyActionBody;


/**
 * . <br>
 * <br>
 */
@Route(resourcePath = "/test_secure_action", methods = HttpMethod.GET)
public class TestSecureAction extends AbstractSecureAction<EmptyActionBody, Person, String> {

    /**
     * {@inheritDoc}
     * @throws UnauthorizedException
     * @see com.visionarts.powerjambda.LambdaAction#handle(java.lang.Object, com.amazonaws.services.lambda.runtime.Context)
     */
    @Override
    public Person handle(ActionRequest<EmptyActionBody> request, Context context)
            throws UnauthorizedException {
        String credential = authenticate(request, context);
        Person p = new Person();
        p.name = credential;
        p.age = 10;
        return p;
    }

    /**
     * {@inheritDoc}
     * @see com.visionarts.powerjambda.actions.AbstractLambdaAction#actionBodyType()
     */
    @Override
    public Class<EmptyActionBody> actionBodyType() {
        return EmptyActionBody.class;
    }

    @Override
    protected String authenticate(ActionRequest<EmptyActionBody> request, Context context)
            throws UnauthorizedException {

        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter("OkCredential"::equals)
                .orElseThrow(() -> new UnauthorizedException("Bad Credentials"));
    }

}
