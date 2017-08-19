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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import org.junit.Test;

import com.visionarts.powerjambda.utils.ReflectionUtils;


public class ReflectionUtilsTest {

    public static enum Types {
        T1,
        ;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    public @interface TestAnnotaion {
        Types value();
    }

    @TestAnnotaion(Types.T1)
    public static class TestAnnotatedClass {
        // empty
    }

    @Test
    public void testFindAllClassesWithAnnotation() throws Exception {
        Set<Class<?>> classes = ReflectionUtils.findAllClassesWithAnnotation(
                ReflectionUtilsTest.class.getPackage().getName(), TestAnnotaion.class);
        assertTrue(classes.contains(TestAnnotatedClass.class));
    }

    @Test
    public void testFindAllClassesWithAnnotationWithCondition() throws Exception {
        Set<Class<?>> classes = ReflectionUtils.findAllClassesWithAnnotation(
                ReflectionUtilsTest.class.getPackage().getName(),
                TestAnnotaion.class,
                c -> c.getAnnotation(TestAnnotaion.class).value() == Types.T1);
        assertTrue(classes.contains(TestAnnotatedClass.class));
    }
}
