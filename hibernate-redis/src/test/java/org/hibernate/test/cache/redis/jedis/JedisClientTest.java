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

package org.hibernate.test.cache.redis.jedis;

import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.test.cache.MultiThreadTestTool;
import org.junit.Test;
import redis.clients.jedis.JedisPool;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.test.cache.redis.jedis.JedisClientTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 1. 오후 12:15
 */
public class JedisClientTest {

    @Test
    public void connectionTest() {
        JedisPool jedisPool = new JedisPool("localhost");
        JedisClient client = new JedisClient(jedisPool);

        assertThat(client.ping()).isEqualToIgnoringCase("pong");
    }

    @Test
    public void jedisPoolTest() {
        final JedisPool jedisPool = new JedisPool("localhost");
        final JedisClient client = new JedisClient(jedisPool);

        MultiThreadTestTool.runTasks(1000, new Runnable() {
            @Override
            public void run() {
                client.ping();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }
        });
    }

    @Test
    public void getAndSet() throws Exception {
        final JedisPool jedisPool = new JedisPool("localhost");
        final JedisClient client = new JedisClient(jedisPool);

        client.set("key", 123);
        assertThat(client.get("key")).isEqualTo(123);
    }
}
