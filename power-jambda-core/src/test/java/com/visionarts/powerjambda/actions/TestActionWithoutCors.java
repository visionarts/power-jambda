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
import com.visionarts.powerjambda.cors.NoneCorsConfiguration;
import com.visionarts.powerjambda.annotations.Route;
import com.visionarts.powerjambda.http.HttpMethod;
import com.visionarts.powerjambda.models.ActionRequest;
import com.visionarts.powerjambda.models.EmptyActionBody;

/**
 * . <br>
 * <br>
 */
@Route(resourcePath = "/test_action_without_cors", methods = HttpMethod.GET, cors = NoneCorsConfiguration.class)
public class TestActionWithoutCors extends AbstractLambdaAction<EmptyActionBody, String> {

    @Override
    public Class<EmptyActionBody> actionBodyType() {
        return EmptyActionBody.class;
    }


    @Override
    public String handle(ActionRequest<EmptyActionBody> request, Context context) {
        return this.getClass().getSimpleName();
    }

}
