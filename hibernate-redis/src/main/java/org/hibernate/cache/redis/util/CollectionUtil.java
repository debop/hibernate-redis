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

package org.hibernate.cache.redis.util;

import java.util.Collection;
import java.util.Set;

/**
 * Collection utility class
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 4. 30. 오후 11:13
 */
public abstract class CollectionUtil {

    private CollectionUtil() {}

    public static String toString(Collection<?> collection) {
        StringBuilder sb = new StringBuilder(collection.size() * 2);

        for (Object item : collection) {
            sb.append(item).append(",");
        }
        // sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    public static byte[][] toArray(Set<byte[]> set) {
        if (set == null || set.size() == 0)
            return new byte[0][];

        return set.toArray(new byte[set.size()][]);
    }
}
