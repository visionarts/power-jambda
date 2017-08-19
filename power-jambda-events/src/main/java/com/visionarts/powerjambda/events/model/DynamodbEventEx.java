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
public class DynamodbEventEx {
    @JsonProperty("Records")
    private List<DynamodbStreamRecord> records;

    /**
     * Gets the list of DynamoDB event records
     *
     */
    public List<DynamodbStreamRecord> getRecords() {
        return records;
    }

    /**
     * Sets the list of DynamoDB event records
     * @param records a list of DynamoDb event records
     */
    public void setRecords(List<DynamodbStreamRecord> records) {
        this.records = records;
    }

    /**
     * The unit of data of an Amazon DynamoDB event
     */
    public static class DynamodbStreamRecord extends RecordEx {
        private String eventSourceARN;

        /**
         * Gets the event source arn of DynamoDB
         *
         */
        public String getEventSourceARN() {
            return eventSourceARN;
        }

        /**
         * Sets the event source arn of DynamoDB
         * @param eventSourceARN A string containing the event source arn
         */
        public void setEventSourceARN(String eventSourceARN) {
            this.eventSourceARN = eventSourceARN;
        }
    }
}
