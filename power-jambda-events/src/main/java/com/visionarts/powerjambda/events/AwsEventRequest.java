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
package com.visionarts.powerjambda.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The input format of Lambda function for AWS Event.
 *
 */
public class AwsEventRequest {

    /** Action */
    private String action;
    /** Event attributes */
    private Map<String, String> attributes;
    /** A string of the request body */
    private String body;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public AwsEventRequest action(String action) {
        setAction(action);
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = new HashMap<>();
        this.attributes.putAll(attributes != null ? attributes : Collections.emptyMap());
    }

    public AwsEventRequest attributes(Map<String, String> attributes) {
        setAttributes(attributes);
        return this;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public AwsEventRequest body(String body) {
        setBody(body);
        return this;
    }

}
