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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.util.RedisCacheUtil;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RMapCache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * RedisClient implemented using Redisson library
 * <p>
 * see https://github.com/mrniko/redisson
 *
 * @author debop sunghyouk.bae@gmail.com
 */
@Slf4j
public class RedisClient {

  public static final String DEFAULT_REGION_NAME = "hibernate";

  @Getter
  private final RedissonClient redisson;

  @Getter
  @Setter
  private int expiryInSeconds;

  public RedisClient() {
    this(Redisson.create());
  }

  public RedisClient(RedissonClient redisson) {
    this(redisson, RedisCacheUtil.DEFAULT_EXPIRY_IN_SECONDS);
  }

  @SneakyThrows
  public RedisClient(@NonNull RedissonClient redisson, int expiryInSeconds) {
    log.trace("RedisClient created. config={}, expiryInSeconds={}", redisson.getConfig().toJSON(), expiryInSeconds);
    this.redisson = redisson;

    if (expiryInSeconds >= 0) {
      this.expiryInSeconds = expiryInSeconds;
    }
  }

  public long dbSize() {
    return redisson.getKeys().count();
  }

  public boolean exists(final String region, final Object key) {
    return getCache(region).containsKey(key);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(final String region, final Object key) {
    T cacheItem = (T) getCache(region).get(key);
    log.trace("retrieve cache item. region={}, key={}, value={}", region, key, cacheItem);
    return cacheItem;
  }

  public boolean isExpired(final String region, final Object key) {
    return exists(region, key);
  }

  public Set<Object> keysInRegion(final String region) {
    return getCache(region).keySet();
  }

  public long keySizeInRegion(final String region) {
    return getCache(region).size();
  }


  public Map<Object, Object> getAll(final String region) {
    return getCache(region);
  }

  public void set(final String region, final Object key, Object value) {
    set(region, key, value, expiryInSeconds);
  }

  public void set(final String region, final Object key, Object value, final long timeoutInSeconds) {
    set(region, key, value, timeoutInSeconds, TimeUnit.SECONDS);
  }

  public void set(final String region, final Object key, Object value, final long timeout, final TimeUnit unit) {
    log.trace("set cache item. region={}, key={}, timeout={}, unit={}",
              region, key, timeout, unit);

    RMapCache<Object, Object> cache = redisson.getMapCache(region);
    if (timeout > 0L) {
      cache.put(key, value, timeout, unit);
    } else {
      cache.put(key, value);
    }
  }

  public void expire(final String region) {
    getCache(region).clearExpire();
  }

  public void del(final String region, final Object key) {
    getCache(region).fastRemove(key);
  }

  public void mdel(final String region, final Collection<?> keys) {
    getCache(region).fastRemove(keys.toArray(new Object[keys.size()]));
  }

  public void deleteRegion(final String region) {
    getCache(region).clear();
  }

  public void flushDb() {
    log.info("flush db...");
    redisson.getKeys().flushdb();
  }

  public boolean isShutdown() {
    return redisson.isShutdown();
  }

  public void shutdown() {
    redisson.shutdown();
  }

  private RMapCache<Object, Object> getCache(final String region) {
    return redisson.getMapCache(region);
  }
}
