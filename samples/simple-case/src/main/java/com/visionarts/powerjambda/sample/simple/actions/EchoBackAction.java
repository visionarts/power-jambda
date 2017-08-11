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
package com.visionarts.powerjambda.sample.simple.actions;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.visionarts.powerjambda.actions.AbstractLambdaAction;
import com.visionarts.powerjambda.annotations.ExceptionHandler;
import com.visionarts.powerjambda.annotations.Route;
import com.visionarts.powerjambda.http.HttpMethod;
import com.visionarts.powerjambda.models.ActionRequest;
import com.visionarts.powerjambda.models.ResponseEntity;
import com.visionarts.powerjambda.sample.simple.actions.models.SampleErrorModel;
import com.visionarts.powerjambda.sample.simple.actions.models.SampleMessageBody;

/**
 * Sample action class. <br>
 * <br>
 */
@Route(resourcePath = "/echo", methods = HttpMethod.POST)
public class EchoBackAction extends AbstractLambdaAction<SampleMessageBody, SampleMessageBody> {

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

    @Override
    public void beforeAction(ActionRequest<SampleMessageBody> request, Context context)
            throws JsonProcessingException {
        if (request.getBody() == null) {
            throw new IllegalArgumentException("Input body is null!");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SampleErrorModel> handleException(IllegalArgumentException e, Context context) {
        logger.error("Catching exception", e);

        SampleErrorModel body = new SampleErrorModel();
        body.error = "BadRequest";
        body.code = 400001;
        body.description = e.getMessage();

        return new ResponseEntity<SampleErrorModel>()
                .body(body)
                .statusCode(400);
    }

}
