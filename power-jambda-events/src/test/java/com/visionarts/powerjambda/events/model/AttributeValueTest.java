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


/**
 * Test case for AttributeValue class. <br>
 * <br>
 */
public class AttributeValueTest {

    private static final ObjectMapper om = new ObjectMapper();

    private AttributeValue attributeValue;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testScalarTypeDeserializationSuccessfully() throws Exception {
        attributeValue = om.readValue("{\"S\": \"Hello\"}", AttributeValue.class);
        assertEquals("Hello", attributeValue.getS());

        attributeValue = om.readValue("{\"N\": \"123.45\"}", AttributeValue.class);
        assertEquals("123.45", attributeValue.getN());

        attributeValue = om.readValue("{\"B\": \"dGhpcyB0ZXh0IGlzIGJhc2U2NC1lbmNvZGVk\"}",
                AttributeValue.class);
        assertEquals(ByteBuffer.wrap(Base64.getDecoder().decode("dGhpcyB0ZXh0IGlzIGJhc2U2NC1lbmNvZGVk")),
                attributeValue.getB());

        attributeValue = om.readValue("{\"BOOL\": true}", AttributeValue.class);
        assertEquals(true, attributeValue.getBool());
        assertEquals(true, attributeValue.isBool());

        attributeValue = om.readValue("{\"NULL\": true}", AttributeValue.class);
        assertEquals(true, attributeValue.getNull());
        assertEquals(true, attributeValue.isNull());

    }

    @Test
    public void testMultiValuedTypeDeserializationSuccessfully() throws Exception {
        attributeValue = om.readValue("{\"SS\": [\"Giraffe\", \"Hippo\" ,\"Zebra\"]}",
                AttributeValue.class);
        assertEquals("Giraffe", attributeValue.getSs().get(0));
        assertEquals("Hippo", attributeValue.getSs().get(1));
        assertEquals("Zebra", attributeValue.getSs().get(2));
        assertEquals(3, attributeValue.getSs().size());

        attributeValue = om.readValue("{\"NS\": [\"42.2\", \"-19\", \"7.5\", \"3.14\"]}",
                AttributeValue.class);
        assertEquals("42.2", attributeValue.getNs().get(0));
        assertEquals("-19", attributeValue.getNs().get(1));
        assertEquals("7.5", attributeValue.getNs().get(2));
        assertEquals("3.14", attributeValue.getNs().get(3));
        assertEquals(4, attributeValue.getNs().size());

        attributeValue = om.readValue("{\"BS\": [\"U3Vubnk=\", \"UmFpbnk=\", \"U25vd3k=\"]}",
                AttributeValue.class);
        assertEquals(ByteBuffer.wrap(Base64.getDecoder().decode("U3Vubnk=")), attributeValue.getBs().get(0));
        assertEquals(ByteBuffer.wrap(Base64.getDecoder().decode("UmFpbnk=")), attributeValue.getBs().get(1));
        assertEquals(ByteBuffer.wrap(Base64.getDecoder().decode("U25vd3k=")), attributeValue.getBs().get(2));
        assertEquals(3, attributeValue.getBs().size());

        attributeValue = om.readValue("{\"M\": {\"Name\": {\"S\": \"Joe\"}, \"Age\": {\"N\": \"35\"}}}",
                AttributeValue.class);
        assertEquals("Joe", attributeValue.getM().get("Name").getS());
        assertEquals("35", attributeValue.getM().get("Age").getN());
        assertEquals(2, attributeValue.getM().size());

        attributeValue = om.readValue("{\"L\": [{\"S\":\"Cookies\"}, {\"S\":\"Coffee\"}, {\"N\":3.14159}]}",
                AttributeValue.class);
        assertEquals("Cookies", attributeValue.getL().get(0).getS());
        assertEquals("Coffee", attributeValue.getL().get(1).getS());
        assertEquals("3.14159", attributeValue.getL().get(2).getN());
        assertEquals(3, attributeValue.getL().size());
    }
}
