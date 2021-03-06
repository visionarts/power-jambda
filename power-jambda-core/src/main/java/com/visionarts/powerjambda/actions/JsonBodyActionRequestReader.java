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

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.AwsProxyRequest;
import com.visionarts.powerjambda.RequestReader;
import com.visionarts.powerjambda.models.ActionRequest;
import com.visionarts.powerjambda.utils.Utils;

public class JsonBodyActionRequestReader<T> extends RequestReader<AwsProxyRequest, ActionRequest<T>> {

    private Class<T> actionBodyType;

    public JsonBodyActionRequestReader(Class<T> actionRequestBody) {
        this.actionBodyType = actionRequestBody;
    }

    @Override
    public ActionRequest<T> readRequest(AwsProxyRequest request) throws IOException {
        ActionRequest<T> newRequest = new ActionRequest<>(request);
        // deserialize body in the Lambda request
        ObjectMapper om = Utils.getObjectMapper();
        T body = null;
        if (request.getBody() != null) {
            body = om.readValue(request.getBody(), actionBodyType);
        }
        newRequest.setBody(body);

        return newRequest;
    }
}
