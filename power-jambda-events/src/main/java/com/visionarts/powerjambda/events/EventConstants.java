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

/**
 * Constants used by the extension for this project.
 *
 */
public class EventConstants {

    public static final String EVENT_SOURCE_DYNAMODB = "aws:dynamodb";
    public static final String EVENT_SOURCE_SNS = "aws:sns";
    public static final String EVENT_SOURCE_KINESIS = "aws:kinesis";
    public static final String EVENT_SOURCE_S3 = "aws:s3";
    public static final String EVENT_SOURCE_SCHEDULED_EVENTS = "aws.events";

    // the following custom definitions

    public static final String EVENT_SOURCE_S3_VIA_SNS = EVENT_SOURCE_S3 + "/" + EVENT_SOURCE_SNS;


    // for logging
    public static final String LOG_THREAD_CONTEXT_EVENT_KEY = "event";

    // CloudWatch Events
    public static final String DETAIL_TYPE_SCHEDULED_EVENT = "Scheduled Event";

    /**
     * The type of data modification that was performed on the DynamoDB table.
     */
    public enum DynamoDBEventName {
        /**
         *  a new item was added to the table.
         */
        INSERT,
        /**
         *  one or more of the item's attributes were updated.
         */
        MODIFY,
        /**
         * the item was deleted from the table
         */
        REMOVE,
    }

    /**
     * DynamoDB Event table required attributes
     */
    public static final String DYNAMODB_ATTR_ACTION = "Action";
    public static final String DYNAMODB_ATTR_REQUEST_BODY = "RequestBody";
    /** Optional attribute for Dynamodb event */
    public static final String DYNAMODB_ATTR_EVENT_ATTRIBUTES = "EventAttrs";

}
