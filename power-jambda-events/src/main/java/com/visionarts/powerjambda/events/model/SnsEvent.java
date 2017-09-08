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
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an Amazon SNS event.<br>
 * <br>
 * Note: <br>
 *  This class is used for ourself deserialization/Unmarshal Sns event using jackson.
 */
public class SnsEvent {

    /**
     * Represents a Sns message
     *
     */
    public static class Sns {
        @JsonProperty("MessageAttributes")
        private Map<String, MessageAttribute> messageAttributes;

        @JsonProperty("SigningCertUrl")
        private String signingCertUrl;

        @JsonProperty("MessageId")
        private String messageId;

        @JsonProperty("Message")
        private String message;

        @JsonProperty("Subject")
        private String subject;

        @JsonProperty("UnsubscribeUrl")
        private String unsubscribeUrl;

        @JsonProperty("Type")
        private String type;

        @JsonProperty("SignatureVersion")
        private String signatureVersion;

        @JsonProperty("Signature")
        private String signature;

        @JsonProperty("Timestamp")
        private String timestamp;

        @JsonProperty("TopicArn")
        private String topicArn;

        /**
         * Gets the attributes associated with the message
         *
         */
        public Map<String, MessageAttribute> getMessageAttributes() {
            return messageAttributes;
        }

        /**
         * Sets the attributes associated with the message
         * @param messageAttributes A map object with string and message attribute key/value pairs
         */
        public void setMessageAttributes(Map<String, MessageAttribute> messageAttributes) {
            this.messageAttributes = messageAttributes;
        }

        /**
         * Gets the URL for the signing certificate
         *
         */
        public String getSigningCertUrl() {
            return signingCertUrl;
        }

        /**
         * Sets the URL for the signing certificate
         * @param signingCertUrl A string containing a URL
         */
        public void setSigningCertUrl(String signingCertUrl) {
            this.signingCertUrl = signingCertUrl;
        }

        /**
         * Gets the message id
         *
         */
        public String getMessageId() {
            return messageId;
        }

        /**
         * Sets the message id
         * @param messageId A string containing the message ID
         */
        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        /**
         * Gets the message
         *
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets the message
         * @param message A string containing the message body
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Gets the subject for the message
         *
         */
        public String getSubject() {
            return subject;
        }

        /**
         * Sets the subject for the message
         * @param subject A string containing the message subject
         */
        public void setSubject(String subject) {
            this.subject = subject;
        }

        /**
         * Gets the message unsubscribe URL
         *
         */
        public String getUnsubscribeUrl() {
            return unsubscribeUrl;
        }

        /**
         * Sets the message unsubscribe URL
         * @param unsubscribeUrl A string with the URL
         */
        public void setUnsubscribeUrl(String unsubscribeUrl) {
            this.unsubscribeUrl = unsubscribeUrl;
        }

        /**
         * Gets the message type
         *
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the message type
         * @param type A string containing the message type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Gets the signature version used to sign the message
         *
         */
        public String getSignatureVersion() {
            return signatureVersion;
        }

        /**
         * The signature version used to sign the message
         * @param signatureVersion A string containing the signature version
         */
        public void setSignatureVersion(String signatureVersion) {
            this.signatureVersion = signatureVersion;
        }

        /**
         * Gets the message signature
         *
         */
        public String getSignature() {
            return signature;
        }

        /**
         * Sets the message signature
         * @param signature A string containing the message signature
         */
        public void setSignature(String signature) {
            this.signature = signature;
        }

        /**
         * Gets the message time stamp
         *
         */
        public String getTimestamp() {
            return timestamp;
        }

        /**
         * Sets the message time stamp
         * @param timestamp A DateTime object representing the message time stamp
         */
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * Gets the topic ARN
         *
         */
        public String getTopicArn() {
            return topicArn;
        }

        /**
         * Sets the topic ARN
         * @param topicArn A string containing the topic ARN
         */
        public void setTopicArn(String topicArn) {
            this.topicArn = topicArn;
        }
    }


    /**
     * Represents a Sns message record. Sns message records are used to send
     * Sns messages to Lambda Functions.
     *
     */
    public static class SnsRecord {
        @JsonProperty("Sns")
        private Sns sns;

        @JsonProperty("EventVersion")
        private String eventVersion;

        @JsonProperty("EventSource")
        private String eventSource;

        @JsonProperty("EventSubscriptionArn")
        private String eventSubscriptionArn;

        /**
         *  Gets the Sns message
         *
         */
        public Sns getSns() {
            return sns;
        }

        /**
         * Sets the Sns message
         * @param sns A Sns object representing the Sns message
         */
        public void setSns(Sns sns) {
            this.sns = sns;
        }

        /**
         * Gets the event version
         *
         */
        public String getEventVersion() {
            return eventVersion;
        }

        /**
         * Sets the event version
         * @param eventVersion A string containing the event version
         */
        public void setEventVersion(String eventVersion) {
            this.eventVersion = eventVersion;
        }

        /**
         * Gets the event source
         *
         */
        public String getEventSource() {
            return eventSource;
        }

        /**
         * Sets the event source
         * @param eventSource A string containing the event source
         */
        public void setEventSource(String eventSource) {
            this.eventSource = eventSource;
        }

        /**
         * Gets the event subscription ARN
         *
         *
         */
        public String getEventSubscriptionArn() {
            return eventSubscriptionArn;
        }

        /**
         * Sets the event subscription ARN
         * @param eventSubscriptionArn A string containing the event subscription ARN
         */
        public void setEventSubscriptionArn(String eventSubscriptionArn) {
            this.eventSubscriptionArn = eventSubscriptionArn;
        }
    }

    /**
     * Represents a Sns message attribute
     *
     */
    public static class MessageAttribute {
        @JsonProperty("Type")
        private String type;
        @JsonProperty("Value")
        private String value;

        /**
         * Gets the attribute type
         *
         */
        public String getType() {
            return type;
        }

        /**
         * Gets the attribute value
         *
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the attribute type
         * @param type A string representing the attribute type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Sets the attribute value
         * @param value A string containing the attribute value
         */
        public void setValue(String value) {
            this.value = value;
        }
    }

    @JsonProperty("Records")
    private List<SnsRecord> records;

    /**
     *  Gets the list of Sns records
     *
     */
    public List<SnsRecord> getRecords() {
        return records;
    }

    /**
     * Sets a list of Sns records
     * @param records A list of Sns record objects
     */
    public void setRecords(List<SnsRecord> records) {
        this.records = records;
    }
}
