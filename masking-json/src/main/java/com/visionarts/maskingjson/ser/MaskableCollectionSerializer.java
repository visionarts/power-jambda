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
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MaskableCollectionSerializer extends MaskableSerializerBase<Collection<?>> {

    public MaskableCollectionSerializer() {
        super(Collection.class, false);
    }

    @Override
    public void serialize(Collection<?> value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartArray(value.size());
        serializeContents(value, gen, provider);
        gen.writeEndArray();
    }

    protected void serializeContents(Collection<?> value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        int i = 0;
        try {
            for (Object elem : value) {
                if (elem != null) {
                    serializeWithMask((String) elem, gen, provider);
                } else {
                    provider.defaultSerializeNull(gen);
                }
                ++i;
            }
        } catch (Exception e) {
            wrapAndThrow(provider, e, value, i);
        }
    }
}
