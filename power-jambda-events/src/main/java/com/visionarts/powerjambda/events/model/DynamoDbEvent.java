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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an Amazon DynamoDB event.<br>
 * <br>
 * Note: <br>
 *  This class is used for ourself deserialization/Unmarshal dynamodb event using jackson.
 */
public class DynamoDbEvent {
    @JsonProperty("Records")
    private List<DynamoDbStreamRecord> records;

    /**
     * Gets the list of DynamoDB event records
     *
     */
    public List<DynamoDbStreamRecord> getRecords() {
        return records;
    }

    /**
     * Sets the list of DynamoDB event records
     * @param records a list of DynamoDb event records
     */
    public void setRecords(List<DynamoDbStreamRecord> records) {
        this.records = records;
    }

    /**
     * The unit of data of an Amazon DynamoDB event
     */
    public static class DynamoDbStreamRecord extends Record {
        @JsonProperty("eventSourceARN")
        private String eventSourceArn;

        /**
         * Gets the event source arn of DynamoDB
         *
         */
        public String getEventSourceArn() {
            return eventSourceArn;
        }

        /**
         * Sets the event source arn of DynamoDB
         * @param eventSourceArn A string containing the event source arn
         */
        public void setEventSourceArn(String eventSourceArn) {
            this.eventSourceArn = eventSourceArn;
        }
    }
}
