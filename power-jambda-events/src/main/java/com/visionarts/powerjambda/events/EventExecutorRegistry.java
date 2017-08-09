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
package com.visionarts.powerjambda.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds references to all of the available EventExecutors.
 *
 */
public class EventExecutorRegistry {

    private final List<AbstractEventExecutor<?, ?>> registry;

    /**
     * Constructs new {@link EventExecutorRegistry} instance with empty registry.
     *
     */
    public EventExecutorRegistry() {
        this(Collections.emptyList());
    }

    /**
     * Constructs new {@link EventExecutorRegistry} instance with given parameter.
     *
     * @param eventExecutors A collection of Executors to register
     */
    public EventExecutorRegistry(List<AbstractEventExecutor<?, ?>> eventExecutors) {
        registry = new ArrayList<>(eventExecutors);
    }

    /**
     * Method for registering a executor that can extend functionality provided by this handler.
     *
     * @param eventExecutor Executor to register
     * @return Returns a reference to this object so that method calls can be chained
     */
    public EventExecutorRegistry register(AbstractEventExecutor<?, ?> eventExecutor) {
        registry.add(eventExecutor);
        return this;
    }

    /**
     * Returns a sequential {@code Stream} with this registry as its source.
     *
     * @return A sequential {@code Stream} over the elements in this registry
     */
    public Stream<AbstractEventExecutor<?, ?>> stream() {
        return registry.stream();
    }
}
