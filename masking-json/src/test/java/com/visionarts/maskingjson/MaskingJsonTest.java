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

package com.visionarts.maskingjson;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visionarts.maskingjson.MaskableObjectMapper;
import com.visionarts.maskingjson.MaskableSensitiveData;

public class MaskingJsonTest {

    public static class MyBean {
        private String userName;

        @MaskableSensitiveData
        private String cardNumber;

        public MyBean() {
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }
    }

    public static class MyBeanWithCollectionList {
        @MaskableSensitiveData
        public String cardNumber;

        @MaskableSensitiveData
        public List<String> cardNumbers;

        public MyBeanWithCollectionList() {
        }
    }

    public static class MyBeanWithNestedStructure {

        public MyBean nestedData = new MyBean();

        public MyBeanWithNestedStructure() {
        }
    }

    /** Test rule. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObjectMapper om;

    @Before
    public void setUp() {
        om = new MaskableObjectMapper().getMapper();
    }

    @Test
    public void testSimpleUsageString() throws Exception {
        MaskableObjectMapper mOm = new MaskableObjectMapper();
        MyBean obj = new MyBean();
        obj.setUserName("test user");
        obj.setCardNumber("4455-7788-9999-7777");
        String json = mOm.writeValueAsString(obj);

        String expectedJson = "{\"userName\":\"test user\",\"cardNumber\":\"*******************\"}";
        assertThat(json, Matchers.is(expectedJson));
    }

    @Test
    public void testStringCollection() throws Exception {
        MyBeanWithCollectionList obj = new MyBeanWithCollectionList();
        obj.cardNumber = "4455-7788-9999-7777";
        obj.cardNumbers = Arrays.asList("4455-7788-9999-7778", null, "4455-7788-9999-77790");

        String json = om.writeValueAsString(obj);
        String expectedJson = "{\"cardNumber\":\"*******************\",\"cardNumbers\":[\"*******************\",null,\"********************\"]}";
        assertThat(json, Matchers.is(expectedJson));
    }

    @Test
    public void testNestedData() throws Exception {
        MyBeanWithNestedStructure obj = new MyBeanWithNestedStructure();
        obj.nestedData.setUserName("test user");
        obj.nestedData.setCardNumber("4455-7788-9999-7777");

        String json = om.writeValueAsString(obj);
        String expectedJson = "{\"nestedData\":{\"userName\":\"test user\",\"cardNumber\":\"*******************\"}}";
        assertThat(json, Matchers.is(expectedJson));
    }

    @Test
    public void testAttachedGetterData() throws Exception {
        Object obj = new Object() {
            private List<String> cardNumbers = new ArrayList<>();
            {
                cardNumbers.add("4455");
            }

            @MaskableSensitiveData
            public List<String> getCardNumbers() {
                return cardNumbers;
            }
        };

        String json = om.writeValueAsString(obj);
        String expectedJson = "{\"cardNumbers\":[\"****\"]}";
        assertThat(json, Matchers.is(expectedJson));
    }

    @Test
    public void testOccurErrorCastError() throws Exception {
        thrown.expect(IOException.class);
        Object obj = new Object() {
            @MaskableSensitiveData
            public Integer number = 1;
        };

        om.writeValueAsString(obj);
    }

    @Test
    public void testOccurErrorCastErrorInCollection() throws Exception {
        thrown.expect(IOException.class);
        Object obj = new Object() {
            @MaskableSensitiveData
            public List<Object> ListIncludeErr = new ArrayList<>();
            {
                ListIncludeErr.add("string");
                ListIncludeErr.add(1); // can not be cast to String type
            }
        };

        om.writeValueAsString(obj);
    }
}
