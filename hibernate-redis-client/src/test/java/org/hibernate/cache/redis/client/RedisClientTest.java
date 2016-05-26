/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.AbstractHibernateRedisTest;
import org.junit.After;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class RedisClientTest extends AbstractHibernateRedisTest {

  private static final String REGION_NAME = "test.hibernate.region";
  private RedisClient client = new RedisClient(createRedisson());

  @After
  public void cleanup() {
    client.deleteRegion(REGION_NAME);
  }

  @Test
  public void testDbSize() {
    assertThat(client.dbSize()).isGreaterThanOrEqualTo(0);
  }

  @Test
  public void simpleValueGetSet() throws Exception {
    client.set(REGION_NAME, "simpleValue", 123);
    Thread.sleep(10);
    assertThat(client.get(REGION_NAME, "simpleValue")).isEqualTo(123);
  }

  @Test
  public void objectReferenceGetSet() throws Exception {
    TestData origin = TestData.of("name", 123);
    client.set(REGION_NAME, "objectRef", origin);

    TestData loaded = client.get(REGION_NAME, "objectRef");
    assertThat(loaded)
        .isNotNull()
        .isEqualTo(origin);
  }

  @Data
  static class TestData implements Serializable {
    private static final long serialVersionUID = -736949543087326836L;

    public static TestData of(String name, int age) {
      TestData instance = new TestData();
      instance.setName(name);
      instance.setAge(age);
      return instance;
    }

    private String name;
    private Integer age;
  }

  @Test
  public void flushDb() {
    client.set(REGION_NAME, "flushDbKey", "value");
    assertThat(client.dbSize()).isGreaterThan(0);
    client.flushDb();
    assertThat(client.dbSize()).isEqualTo(0);
  }

  @Test
  public void deleteCacheItem() {
    client.set(REGION_NAME, "deleteKey", "value");
    assertThat(client.exists(REGION_NAME, "deleteKey")).isTrue();
    assertThat(client.get(REGION_NAME, "deleteKey")).isEqualTo("value");

    client.del(REGION_NAME, "deleteKey");
    assertThat(client.exists(REGION_NAME, "deleteKey")).isFalse();
  }

  @Test
  public void multiDelete() {
    client.deleteRegion(REGION_NAME);

    int count = 100;
    List<String> keys = new ArrayList<String>();
    for (int i = 0; i < count; i++) {
      String key = "multiDelete-Key-" + i;
      client.set(REGION_NAME, key, i);
      keys.add(key);
    }

    assertThat(client.keySizeInRegion(REGION_NAME)).isEqualTo(count);
    client.mdel(REGION_NAME, keys);
    assertThat(client.keySizeInRegion(REGION_NAME)).isEqualTo(0);
  }

  @Test
  public void keysInRegion() {
    client.deleteRegion(REGION_NAME);

    assertThat(client.keysInRegion(REGION_NAME)).isEmpty();

    int count = 100;
    List<String> keys = new ArrayList<String>();
    for (int i = 0; i < count; i++) {
      String key = "multiDelete-Key-" + i;
      client.set(REGION_NAME, key, i);
      keys.add(key);
    }

    Set<Object> loadedKeys = client.keysInRegion(REGION_NAME);
    assertThat(loadedKeys).isNotEmpty().hasSize(keys.size());

    client.deleteRegion(REGION_NAME);
    assertThat(client.keysInRegion(REGION_NAME)).isEmpty();
    assertThat(client.keySizeInRegion(REGION_NAME)).isEqualTo(0);
  }

}
