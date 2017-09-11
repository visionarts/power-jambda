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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * Represents the data for an attribute. You can set one, and only one, of the elements.
 * </p>
 * Note: <br>
 *  This class is used for ourself deserialization/Unmarshal dynamodb event using jackson.
 */
public class AttributeValue {
    @JsonProperty("S")
    private String s;
    @JsonProperty("N")
    private String n;
    @JsonProperty("B")
    private ByteBuffer b;
    @JsonProperty("SS")
    private List<String> sS;
    @JsonProperty("NS")
    private List<String> nS;
    @JsonProperty("BS")
    private List<ByteBuffer> bS;
    @JsonProperty("M")
    private Map<String, AttributeValue> m;
    @JsonProperty("L")
    private List<AttributeValue> l;
    @JsonProperty("NULL")
    private Boolean nullValue;
    @JsonProperty("BOOL")
    private Boolean bool;

    public AttributeValue() {
    }

    public AttributeValue(String s) {
        setS(s);
    }

    public AttributeValue(List<String> sS) {
        setSs(sS);
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getS() {
        return this.s;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getN() {
        return this.n;
    }

    public void setB(ByteBuffer b) {
        this.b = b;
    }

    public ByteBuffer getB() {
        return this.b;
    }

    public List<String> getSs() {
        return this.sS;
    }

    public void setSs(Collection<String> sS) {
        if (sS == null) {
            this.sS = null;
            return;
        }
        this.sS = new ArrayList<>(sS);
    }

    public List<String> getNs() {
        return this.nS;
    }

    public void setNs(Collection<String> nS) {
        if (nS == null) {
            this.nS = null;
            return;
        }
        this.nS = new ArrayList<>(nS);
    }

    public List<ByteBuffer> getBs() {
        return this.bS;
    }

    public void setBs(Collection<ByteBuffer> bS) {
        if (bS == null) {
            this.bS = null;
            return;
        }
        this.bS = new ArrayList<>(bS);
    }

    public Map<String, AttributeValue> getM() {
        return this.m;
    }

    public void setM(Map<String, AttributeValue> m) {
        this.m = m;
    }

    public List<AttributeValue> getL() {
        return this.l;
    }

    public void setL(Collection<AttributeValue> l) {
        if (l == null) {
            this.l = null;
            return;
        }
        this.l = new ArrayList<>(l);
    }

    public void setNull(Boolean nullValue) {
        this.nullValue = nullValue;
    }

    public Boolean getNull() {
        return this.nullValue;
    }

    public Boolean isNull() {
        return this.nullValue;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public Boolean getBool() {
        return this.bool;
    }

    public Boolean isBool() {
        return this.bool;
    }
}
