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

package org.hibernate.test.cache.jedis;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.test.cache.MultiThreadTestTool;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * {@link org.hibernate.cache.redis.jedis.JedisClient} 테스트
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 1. 오후 3:05
 */
public class JedisClientTest {

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    private JedisPool jedisPool;
    private JedisClient client = new JedisClient();

    @After
    public void after() {
        client.flushDb();
    }

    @Test
    public void connectionTest() {
        assertThat(client.ping()).isEqualToIgnoringCase("pong");
    }

    @Test
    public void jedisPoolTest() {
        MultiThreadTestTool.runTasks(100, new Runnable() {
            @Override
            public void run() {
                client.ping();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}
            }
        });
    }

    @Test
    public void getAndSet() throws Exception {
        client.set(JedisClient.DEFAULT_REGION_NAME, "key", 123, -1);
        assertThat(client.get(JedisClient.DEFAULT_REGION_NAME, "key")).isEqualTo(123);
    }

    @Test
    public void expireTest() throws Exception {
        client.set(JedisClient.DEFAULT_REGION_NAME, "expireTest", "Value", 1);
        assertThat(client.get(JedisClient.DEFAULT_REGION_NAME, "expireTest")).isEqualTo("Value");
        Thread.sleep(1500);
        client.expire(JedisClient.DEFAULT_REGION_NAME);
        assertThat(client.get(JedisClient.DEFAULT_REGION_NAME, "expireTest")).isNull();
    }

    @Test
    public void flushDbTest() throws Exception {
        client.set(JedisClient.DEFAULT_REGION_NAME, "a", "a");
        assertThat(client.dbSize()).isGreaterThan(0);
        client.flushDb();
        assertThat(client.dbSize()).isEqualTo(0);
    }

    @Test
    public void deleteTest() throws Exception {
        client.set(JedisClient.DEFAULT_REGION_NAME, "d", "d");
        assertThat(client.get(JedisClient.DEFAULT_REGION_NAME, "d")).isEqualTo("d");
        assertThat(client.exists(JedisClient.DEFAULT_REGION_NAME, "d")).isTrue();

        client.del(JedisClient.DEFAULT_REGION_NAME, "d");
        assertThat(client.get(JedisClient.DEFAULT_REGION_NAME, "d")).isNull();
        assertThat(client.exists(JedisClient.DEFAULT_REGION_NAME, "d")).isFalse();
    }

    @Test
    public void mgetTest() throws Exception {
        int count = 100;
        List<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            client.set(JedisClient.DEFAULT_REGION_NAME, i, i, -1);
            keys.add(i);
        }
        List<Object> values = client.mget(JedisClient.DEFAULT_REGION_NAME, keys);
        assertThat(values.size()).isEqualTo(count);
    }

    @Test
    public void mdelTest() throws Exception {
        int count = 100;
        List<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            client.set(JedisClient.DEFAULT_REGION_NAME, i, i, -1);
            keys.add(i);
        }
        List<Object> values = client.mget(JedisClient.DEFAULT_REGION_NAME, keys);
        assertThat(values.size()).isEqualTo(count);

        client.mdel(JedisClient.DEFAULT_REGION_NAME, keys);
        for (int i = 0; i < count; i++) {
            assertThat(client.get(JedisClient.DEFAULT_REGION_NAME, i)).isNull();
        }
    }


    @Test
    public void keysInRegion() throws Exception {
        client.flushDb();

        int count = 100;
        List<Integer> keys = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            client.set(JedisClient.DEFAULT_REGION_NAME, i, i, -1);
            keys.add(i);
        }

        Set<Object> keysInRegion = client.keysInRegion(JedisClient.DEFAULT_REGION_NAME);
        assertThat(keysInRegion.size()).isEqualTo(count);

        client.deleteRegion(JedisClient.DEFAULT_REGION_NAME);
        keysInRegion = client.keysInRegion(JedisClient.DEFAULT_REGION_NAME);
        assertThat(keysInRegion.size()).isEqualTo(0);
    }
}
