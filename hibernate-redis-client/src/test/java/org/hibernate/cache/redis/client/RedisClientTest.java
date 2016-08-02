/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.hibernate.cache.redis.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
    Assertions.assertThat(client.<Integer>get(REGION_NAME, "simpleValue")).isEqualTo(123);
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
    assertThat(client.<String>get(REGION_NAME, "deleteKey")).isEqualTo("value");

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
