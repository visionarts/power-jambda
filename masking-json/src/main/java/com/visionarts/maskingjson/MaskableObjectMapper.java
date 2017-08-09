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

import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;

/**
 * MaskableObjectMapper provides functionality for writing masked JSON.
 * <p>
 * Simplest usage is of form:
 * <pre>
 *  final MaskableObjectMapper mapper = new MaskableObjectMapper(); // can use static singleton, inject: just make sure to reuse!
 *  MyValueIncludeSensitiveData value = new MyValueIncludeSensitiveData();
 *  mapper.writeValueAsString(value); // writes masked JSON serialization of MyValueIncludeSensitiveData instance
 * </pre>
 */
public class MaskableObjectMapper {

    private final ObjectMapper mapper;

    public MaskableObjectMapper() {
        this(new ObjectMapper());
    }

    public MaskableObjectMapper(ObjectMapper mapper) {
        Objects.requireNonNull(mapper);
        setAnnotationIntrospectors(mapper);
        this.mapper = mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Convenience method, equivalent to calling:
     * <pre>
     *  getMapper().writeValueAsString(value);
     * </pre>
     *
     * @param value to mask
     * @return A masked string
     * @throws JsonProcessingException if processing (parsing, generating) JSON content
     *                                 that are not pure I/O problems
     */
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    private void setAnnotationIntrospectors(ObjectMapper mapper) {
        AnnotationIntrospector curSerIntro = mapper.getSerializationConfig().getAnnotationIntrospector();
        AnnotationIntrospector newSerIntro = AnnotationIntrospectorPair.pair(curSerIntro,
                new MaskableSensitiveDataAnnotationIntrospector());
        mapper.setAnnotationIntrospector(newSerIntro);
    }
}
