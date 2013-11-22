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

import java.nio.charset.Charset;

/**
 * Serializer for Redis Key or Value
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 9 오후 10:20
 */
public interface RedisSerializer<T> {

    public byte[] EMPTY_BYTES = new byte[0];

    public Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * Serialize Object
     */
    byte[] serialize(final T graph);

    /**
     * Deserialize to object
     */
    T deserialize(final byte[] bytes);
}
