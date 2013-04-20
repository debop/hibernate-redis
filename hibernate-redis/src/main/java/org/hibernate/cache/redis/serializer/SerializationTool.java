/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis.serializer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Serialization 툴 (복수의 값을 한번에 직렬화/역직렬화를 수행합니다)
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 11:38
 */
public abstract class SerializationTool {

    static final byte[] EMPTY_ARRAY = new byte[0];

    static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    @SuppressWarnings("unchecked")
    static <T extends Collection<?>> T deserializeValues(Collection<byte[]> rawValues,
                                                         Class<T> clazz,
                                                         RedisSerializer<?> redisSerializer) {

        if (rawValues == null)
            return null;

        int valueCount = rawValues.size();
        Collection<Object> values =
                List.class.isAssignableFrom(clazz)
                        ? Lists.newArrayListWithCapacity(valueCount)
                        : Sets.newLinkedHashSetWithExpectedSize(valueCount);

        for (byte[] bs : rawValues) {
            values.add(redisSerializer.deserialize(bs));
        }
        return (T) values;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> deserialize(Set<byte[]> rawValues, RedisSerializer<T> redisSerializer) {
        return deserializeValues(rawValues, Set.class, redisSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> deserialize(List<byte[]> rawValues, RedisSerializer<T> redisSerializer) {
        return deserializeValues(rawValues, List.class, redisSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> deserialize(Collection<byte[]> rawValues, RedisSerializer<T> redisSerializer) {
        return deserializeValues(rawValues, List.class, redisSerializer);
    }
}
