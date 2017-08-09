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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.visionarts.powerjambda.utils.FunctionalUtils;

/**
 * Test case for FunctionalUtils.
 *
 */
public class FunctionalUtilsTest {

    @Test
    public void isEmptyTest() {
        assertEquals(true, FunctionalUtils.isEmpty((Collection<?>)null));
        assertEquals(true, FunctionalUtils.isEmpty(Collections.emptyList()));

        assertEquals(true, FunctionalUtils.isEmpty((String)null));
        assertEquals(true, FunctionalUtils.isEmpty(""));
    }

    @Test
    public void isNotEmptyTest() {
        assertEquals(true, FunctionalUtils.isNotEmpty(Arrays.asList("a")));

        assertEquals(true, FunctionalUtils.isNotEmpty("a"));
    }
}
