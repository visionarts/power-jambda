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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.visionarts.powerjambda.utils.FunctionalUtils.toBiConsumer;
import static com.visionarts.powerjambda.utils.FunctionalUtils.toConsumer;
import static org.junit.Assert.assertEquals;

/**
 * Test case for FunctionalUtils.
 *
 */
public class FunctionalUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Logger logger = LogManager.getLogger(FunctionalUtilsTest.class);

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

    @Test
    public void canUseConsumerThatThrowsCheckedExceptionInLambda() {
        Stream.of(false).forEach(toConsumer(this::consumerMethodWithChecked, logger));
    }

    @Test
    public void exceptionsForConsumersAreConverted() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Duh, something went wrong");

        Stream.of(true).forEach(toConsumer(this::consumerMethodWithChecked, logger));
    }

    @Test
    public void canUseBiConsumerThatThrowsCheckedExceptionInLambda() {
        ImmutableMap.of(false, "dummy")
                .forEach(toBiConsumer(this::biConsumerMethodWithChecked, logger));
    }

    @Test
    public void exceptionsForBiConsumersAreConverted() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Duh, something went wrong dummy");

        ImmutableMap.of(true, "dummy")
                .forEach(toBiConsumer(this::biConsumerMethodWithChecked, logger));
    }

    private void consumerMethodWithChecked(Boolean shouldThrow) throws Exception {
        if (shouldThrow) {
            throw new Exception("Duh, something went wrong");
        }
    }

    private void biConsumerMethodWithChecked(Boolean shouldThrow, String dummy) throws Exception {
        if (shouldThrow) {
            throw new Exception("Duh, something went wrong " + dummy);
        }
    }
}
