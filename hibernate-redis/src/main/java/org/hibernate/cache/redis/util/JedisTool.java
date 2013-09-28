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

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.jedis.JedisClient;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

/**
 * org.hibernate.cache.redis.util.JedisTool
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 2. 오전 1:53
 */
@Slf4j
public final class JedisTool {

    private JedisTool() { }

    /**
     * {@link redis.clients.jedis.JedisPool} 을 생성합니다.
     */
    public static JedisPool createJedisPool(Properties props) {

        String host = props.getProperty("redis.host", "localhost");
        Integer port = Integer.decode(props.getProperty("redis.port", "6379"));
        Integer timeout = Integer.decode(props.getProperty("redis.timeout", "2000")); // msec
        String password = props.getProperty("redis.password", null);
        Integer database = Integer.decode(props.getProperty("redis.database", "0"));

        log.info("JedisPool을 생성합니다... host=[{}], port=[{}], timeout=[{}], password=[{}], database=[{}]",
                 host, port, timeout, password, database);

        return new JedisPool(createJedisPoolConfig(), host, port, timeout, password, database);
    }

    /**
     * {@link org.hibernate.cache.redis.jedis.JedisClient} 를 생성합니다.
     */
    public static JedisClient createJedisClient(Properties props) {
        Integer expiryInSeconds = Integer.decode(props.getProperty("redis.expiryInSeconds", "120"));  // 120 seconds
        return new JedisClient(createJedisPool(props), expiryInSeconds);
    }

    private static JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxActive(32);
        poolConfig.setMinIdle(2);
        return poolConfig;
    }
}
