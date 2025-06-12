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

package com.visionarts.powerjambda.utils;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.Test;


/**
 * Test case for EnvironmentVariableUtils.
 *
 */
public class EnvironmentVariableUtilsTest {

    public static class TestConfig {
        public String TEST_STRING_VALUE = "dummy";
        public Integer TEST_INTEGER_VALUE = 100;
        public int TEST_INT_VALUE = 100;
        public Boolean TEST_BOOLEAN_VALUE = true;
        public boolean TEST_BOOL_VALUE = true;
        public String TEST_NOT_EXIST_VALUE = "not_exists";

        public void set(String value) {
            this.TEST_STRING_VALUE = value;
        }
    }

    public static class TestConfigNotSupported {
        public String TEST_STRING_VALUE = "dummy";
        public Map<String, String> TEST_MAP_VALUE;
    }

    private static final Map<String, String> TestEnvValues = new HashMap<>();
    private static final Function<String, String> mockFetcher = key -> TestEnvValues.get(key);
    static {
        TestEnvValues.put("TEST_STRING_VALUE", "string_value");
        TestEnvValues.put("TEST_INTEGER_VALUE", "1");
        TestEnvValues.put("TEST_INT_VALUE", "1");
        TestEnvValues.put("TEST_BOOLEAN_VALUE", "false");
        TestEnvValues.put("TEST_BOOL_VALUE", "false");
        TestEnvValues.put("TEST_MAP_VALUE", "");
    }

    @Test
    public void newConfigurationInstanceTest() {
        TestConfig config = EnvironmentVariableUtils.newConfigurationInstance(TestConfig.class, mockFetcher);
        assertEquals("string_value", config.TEST_STRING_VALUE);
        assertEquals(Integer.valueOf(1), config.TEST_INTEGER_VALUE);
        assertEquals(1, config.TEST_INT_VALUE);
        assertEquals(Boolean.FALSE, config.TEST_BOOLEAN_VALUE);
        assertEquals(false, config.TEST_BOOL_VALUE);
        assertEquals("not_exists", config.TEST_NOT_EXIST_VALUE);
    }

    @Test
    public void newConfigurationInstanceNotSupportedTest() {
        assertThrows(RuntimeException.class, () -> {
            EnvironmentVariableUtils.newConfigurationInstance(TestConfigNotSupported.class, mockFetcher);
        });
    }
}
