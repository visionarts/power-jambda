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

package com.visionarts.powerjambda.filters;

import java.util.HashMap;
import java.util.Map;

public class FilterChain<T> implements Filter<T> {

    private Map<Class<?>, Filter<T>> filters = new HashMap<>();

    public void addFilter(Filter<T> filter) {
        filters.put(filter.getClass(), filter);
    }

    @Override
    public void filter(T t) {
        filters.values().forEach(f -> f.filter(t));
    }
}
