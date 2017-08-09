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

/**
 * Implementations of the RequestReader object.
 *
 * @param <EventType> The input type of the Lambda function
 * @param <Request> The type of the action request object
 */
public interface EventRequestReader<EventType, Request> {

    /**
     * Reads the incoming request and produces a populated request for the action.
     *
     * @param event The incoming request object
     * @return A request object for the action
     */
    Request readEvent(EventType event);
}
