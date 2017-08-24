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

package com.visionarts.maskingjson.ser;

import java.io.IOException;
import java.util.stream.IntStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Base class for serializers used for handling supported types to be mask.
 *
 */
public abstract class MaskableSerializerBase<T> extends StdSerializer<T> {

    private static final char DEFAULT_MASK_CHARACTER = '*';

    private char maskingValue = DEFAULT_MASK_CHARACTER;

    protected MaskableSerializerBase(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected MaskableSerializerBase(Class<T> t) {
        super(t);
    }

    protected void serializeWithMask(String value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, value.length()).forEach(i -> sb.append(maskingValue));
        gen.writeString(sb.toString());
    }

    public void setMaskingValue(char value) {
        this.maskingValue = value;
    }
}
