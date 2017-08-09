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

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Base64;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.powerjambda.events.model.AttributeValueEx;


/**
 * Test case for AttributeValueEx class. <br>
 * <br>
 */
public class AttributeValueExTest {

    private static final ObjectMapper om = new ObjectMapper();

    private AttributeValueEx attributeValueEx;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testScalarTypeDeserializationSuccessfully() throws Exception {
        attributeValueEx = om.readValue("{\"S\": \"Hello\"}", AttributeValueEx.class);
        assertEquals("Hello", attributeValueEx.getS());

        attributeValueEx = om.readValue("{\"N\": \"123.45\"}", AttributeValueEx.class);
        assertEquals("123.45", attributeValueEx.getN());

        attributeValueEx = om.readValue("{\"B\": \"dGhpcyB0ZXh0IGlzIGJhc2U2NC1lbmNvZGVk\"}",
                AttributeValueEx.class);
        assertEquals(ByteBuffer.wrap(Base64.getDecoder().decode("dGhpcyB0ZXh0IGlzIGJhc2U2NC1lbmNvZGVk")),
                attributeValueEx.getB());

        attributeValueEx = om.readValue("{\"BOOL\": true}", AttributeValueEx.class);
        assertEquals(true, attributeValueEx.getBOOL());
        assertEquals(true, attributeValueEx.isBOOL());

        attributeValueEx = om.readValue("{\"NULL\": true}", AttributeValueEx.class);
        assertEquals(true, attributeValueEx.getNULL());
        assertEquals(true, attributeValueEx.isNULL());

    }

    @Test
    public void testMultiValuedTypeDeserializationSuccessfully() throws Exception {
        attributeValueEx = om.readValue("{\"SS\": [\"Giraffe\", \"Hippo\" ,\"Zebra\"]}",
                AttributeValueEx.class);
        assertEquals("Giraffe", attributeValueEx.getSS().get(0));
        assertEquals("Hippo", attributeValueEx.getSS().get(1));
        assertEquals("Zebra", attributeValueEx.getSS().get(2));
        assertEquals(3, attributeValueEx.getSS().size());

        attributeValueEx = om.readValue("{\"NS\": [\"42.2\", \"-19\", \"7.5\", \"3.14\"]}",
                AttributeValueEx.class);
        assertEquals("42.2", attributeValueEx.getNS().get(0));
        assertEquals("-19", attributeValueEx.getNS().get(1));
        assertEquals("7.5", attributeValueEx.getNS().get(2));
        assertEquals("3.14", attributeValueEx.getNS().get(3));
        assertEquals(4, attributeValueEx.getNS().size());

        attributeValueEx = om.readValue("{\"BS\": [\"U3Vubnk=\", \"UmFpbnk=\", \"U25vd3k=\"]}",
                AttributeValueEx.class);
        assertEquals(ByteBuffer.wrap(Base64.getDecoder().decode("U3Vubnk=")), attributeValueEx.getBS().get(0));
        assertEquals(ByteBuffer.wrap(Base64.getDecoder().decode("UmFpbnk=")), attributeValueEx.getBS().get(1));
        assertEquals(ByteBuffer.wrap(Base64.getDecoder().decode("U25vd3k=")), attributeValueEx.getBS().get(2));
        assertEquals(3, attributeValueEx.getBS().size());

        attributeValueEx = om.readValue("{\"M\": {\"Name\": {\"S\": \"Joe\"}, \"Age\": {\"N\": \"35\"}}}",
                AttributeValueEx.class);
        assertEquals("Joe", attributeValueEx.getM().get("Name").getS());
        assertEquals("35", attributeValueEx.getM().get("Age").getN());
        assertEquals(2, attributeValueEx.getM().size());

        attributeValueEx = om.readValue("{\"L\": [{\"S\":\"Cookies\"}, {\"S\":\"Coffee\"}, {\"N\":3.14159}]}",
                AttributeValueEx.class);
        assertEquals("Cookies", attributeValueEx.getL().get(0).getS());
        assertEquals("Coffee", attributeValueEx.getL().get(1).getS());
        assertEquals("3.14159", attributeValueEx.getL().get(2).getN());
        assertEquals(3, attributeValueEx.getL().size());
    }
}
