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

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.visionarts.powerjambda.utils.Utils;

/**
 * Represents a processing result.
 *
 */
public class Result<T> {

    private List<T> successItems = new ArrayList<>();
    private List<T> failureItems = new ArrayList<>();
    private List<T> skippedItems = new ArrayList<>();
    @JsonIgnore
    private boolean throwOnFailure;

    public List<T> getSuccessItems() {
        return successItems;
    }

    public void setSuccessItems(List<T> success) {
        this.successItems = success;
    }

    public void addSuccessItem(T item) {
        successItems.add(item);
    }

    public List<T> getFailureItems() {
        return failureItems;
    }

    public void setFailureItems(List<T> failures) {
        this.failureItems = failures;
    }

    public void addFailureItem(T item) {
        failureItems.add(item);
    }

    public List<T> getSkippedItems() {
        return skippedItems;
    }

    public void setSkippedItems(List<T> skipped) {
        this.skippedItems = skipped;
    }

    public void addSkippedItem(T item) {
        skippedItems.add(item);
    }

    public void setThrowOnFailure(boolean throwOnFailure) {
        this.throwOnFailure = throwOnFailure;
    }

    @JsonIgnore
    public <E extends Throwable> void throwOnFailure(Supplier<E> supplier) throws E {
        if (throwOnFailure && !failureItems.isEmpty()) {
            throw supplier.get();
        }
    }

    @JsonIgnore
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("successItems = ").append(successItems.size())
            .append(", failureItems = ").append(failureItems.size())
            .append(", skippedItems = ").append(skippedItems.size());
        return sb.toString();
    }

    /**
     * Returns a string representation of this object; useful for testing and
     * debugging.
     *
     * @return A string representation of this object
     */
    @Override
    public String toString() {
        try {
            return Utils.getObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

}
