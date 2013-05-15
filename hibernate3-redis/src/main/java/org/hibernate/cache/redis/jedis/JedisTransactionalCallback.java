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

import redis.clients.jedis.Transaction;

/**
 * Jedis 의 Transaction을 이용하여 복수의 작업을 수행하도록 합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 1. 오후 2:20
 */
public interface JedisTransactionalCallback {
    /**
     * Transaction 하에서 작업을 수행합니다.
     *
     * @param tx Jedis Transaction
     */
    public void execute(Transaction tx);
}
