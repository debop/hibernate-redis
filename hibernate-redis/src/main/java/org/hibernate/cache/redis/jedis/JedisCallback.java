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

package org.hibernate.cache.redis.jedis;

import redis.clients.jedis.Jedis;

/**
 * Callback for Jedis task
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 10. 오전 10:22
 */
public interface JedisCallback<T> {
    /**
     * execute jedis task
     *
     * @param jedis Jedis instance.
     * @return return value
     */
    public T execute(Jedis jedis);
}
