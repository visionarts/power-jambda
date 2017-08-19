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
public class AttributeValueEx {
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
    private Map<String, AttributeValueEx> m;
    @JsonProperty("L")
    private List<AttributeValueEx> l;
    @JsonProperty("NULL")
    private Boolean nULLValue;
    @JsonProperty("BOOL")
    private Boolean bOOL;

    public AttributeValueEx() {
    }

    public AttributeValueEx(String s) {
        setS(s);
    }

    public AttributeValueEx(List<String> sS) {
        setSS(sS);
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

    public List<String> getSS() {
        return this.sS;
    }

    public void setSS(Collection<String> sS) {
        if (sS == null) {
            this.sS = null;
            return;
        }
        this.sS = new ArrayList<>(sS);
    }

    public List<String> getNS() {
        return this.nS;
    }

    public void setNS(Collection<String> nS) {
        if (nS == null) {
            this.nS = null;
            return;
        }
        this.nS = new ArrayList<>(nS);
    }

    public List<ByteBuffer> getBS() {
        return this.bS;
    }

    public void setBS(Collection<ByteBuffer> bS) {
        if (bS == null) {
            this.bS = null;
            return;
        }
        this.bS = new ArrayList<>(bS);
    }

    public Map<String, AttributeValueEx> getM() {
        return this.m;
    }

    public void setM(Map<String, AttributeValueEx> m) {
        this.m = m;
    }

    public List<AttributeValueEx> getL() {
        return this.l;
    }

    public void setL(Collection<AttributeValueEx> l) {
        if (l == null) {
            this.l = null;
            return;
        }
        this.l = new ArrayList<>(l);
    }

    public void setNULL(Boolean nULLValue) {
        this.nULLValue = nULLValue;
    }

    public Boolean getNULL() {
        return this.nULLValue;
    }

    public Boolean isNULL() {
        return this.nULLValue;
    }

    public void setBOOL(Boolean bOOL) {
        this.bOOL = bOOL;
    }

    public Boolean getBOOL() {
        return this.bOOL;
    }

    public Boolean isBOOL() {
        return this.bOOL;
    }
}
