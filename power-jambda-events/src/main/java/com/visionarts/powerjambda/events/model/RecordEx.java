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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A description of a unique event within a stream.
 * </p>
 * Note: <br>
 *  This class is used for ourself deserialization/Unmarshal dynamodb event using jackson.
 */
public class RecordEx {
    private String eventID;
    private String eventName;
    private String eventVersion;
    private String eventSource;
    private String awsRegion;
    private StreamRecordEx dynamodb;

    public RecordEx() {
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventID() {
        return this.eventID;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventVersion(String eventVersion) {
        this.eventVersion = eventVersion;
    }

    public String getEventVersion() {
        return this.eventVersion;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public String getEventSource() {
        return this.eventSource;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public String getAwsRegion() {
        return this.awsRegion;
    }

    public void setDynamodb(StreamRecordEx dynamodb) {
        this.dynamodb = dynamodb;
    }

    public StreamRecordEx getDynamodb() {
        return this.dynamodb;
    }

    public static class StreamRecordEx {
        @JsonProperty("ApproximateCreationDateTime")
        private Long approximateCreationDateTime;
        @JsonProperty("Keys")
        private Map<String, AttributeValueEx> keys;
        @JsonProperty("NewImage")
        private Map<String, AttributeValueEx> newImage;
        @JsonProperty("OldImage")
        private Map<String, AttributeValueEx> oldImage;
        @JsonProperty("SequenceNumber")
        private String sequenceNumber;
        @JsonProperty("SizeBytes")
        private Long sizeBytes;
        @JsonProperty("StreamViewType")
        private String streamViewType;

        public StreamRecordEx() {
        }

        public void setApproximateCreationDateTime(Long approximateCreationDateTime) {
            this.approximateCreationDateTime = approximateCreationDateTime;
        }

        public Long getApproximateCreationDateTime() {
            return this.approximateCreationDateTime;
        }

        public Map<String, AttributeValueEx> getKeys() {
            return this.keys;
        }

        public void setKeys(Map<String, AttributeValueEx> keys) {
            this.keys = keys;
        }

        public Map<String, AttributeValueEx> getNewImage() {
            return this.newImage;
        }

        public void setNewImage(Map<String, AttributeValueEx> newImage) {
            this.newImage = newImage;
        }

        public Map<String, AttributeValueEx> getOldImage() {
            return this.oldImage;
        }

        public void setOldImage(Map<String, AttributeValueEx> oldImage) {
            this.oldImage = oldImage;
        }

        public void setSequenceNumber(String sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public String getSequenceNumber() {
            return this.sequenceNumber;
        }

        public void setSizeBytes(Long sizeBytes) {
            this.sizeBytes = sizeBytes;
        }

        public Long getSizeBytes() {
            return this.sizeBytes;
        }

        public void setStreamViewType(String streamViewType) {
            this.streamViewType = streamViewType;
        }

        public String getStreamViewType() {
            return this.streamViewType;
        }
    }

}
