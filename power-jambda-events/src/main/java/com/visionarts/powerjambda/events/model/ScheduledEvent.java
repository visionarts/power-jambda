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
package com.visionarts.powerjambda.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a ScheduledEvent in AWS CloudWatch Events.<br>
 * <br>
 * Note: <br>
 *  This class is used for ourself deserialization/Unmarshal ScheduledEvent using jackson.
 */
public class ScheduledEvent {

    public String source;

    @JsonProperty("detail-type")
    public String detailType;

    public JsonNode detail;
}
