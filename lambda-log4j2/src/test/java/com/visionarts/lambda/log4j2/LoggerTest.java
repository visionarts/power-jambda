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

package com.visionarts.lambda.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.junit.After;
import org.junit.Test;

public class LoggerTest {

    private Logger logger = LogManager.getLogger(this.getClass().getName());

    @After
    public void tearDown() {
        ThreadContext.clearAll();
    }

    @Test
    public void testLogMessages() throws Exception {
        ThreadContext.put("case", "Test0");
        logger.info("this message has disappeared");
        logger.error("errorMessage {} {}", "i0", "i1");
        logger.error("errorMessage", () -> new Exception("exceptionMsg"));
    }
}
