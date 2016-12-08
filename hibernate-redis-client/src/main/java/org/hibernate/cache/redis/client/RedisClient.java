/*
 * Copyright (c) 2016. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.cache.redis.client;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.util.RedisCacheUtil;
import org.redisson.api.RClusteredMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * RedisClient implemented using Redisson library
 * <p>
 * see https://github.com/mrniko/redisson
 *
 * WARNING: no default constructor to avoid automatically creating Redis client.
 *
 * @author debop sunghyouk.bae@gmail.com
 */
@Slf4j
public class RedisClient {

  @Getter
  private final transient RedissonClient redisson;

  @SneakyThrows
  public RedisClient(@NonNull RedissonClient redisson) {
    log.trace("RedisClient created. config={}", redisson.getConfig().toJSON());
    this.redisson = redisson;

  }

  public long nextTimestamp(final List<Object> keys) {
    return redisson.getScript().eval(RScript.Mode.READ_WRITE,
            "redis.call('setnx', KEYS[1], ARGV[1]); " +
            "return redis.call('incr', KEYS[1]);",
            RScript.ReturnType.INTEGER, keys, System.currentTimeMillis());
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
    log.trace("set cache item. region={}, key={}",
              region, key);

    RClusteredMap<Object, Object> cache = getCache(region);
    //TODO(jontejj): Removed because of CROSSSLOT issues redisson PRO is working on such a structure for 2017
    //if (timeout > 0L) {
    //  cache.fastPut(key, value, timeout, unit);
    //} else {
      cache.fastPut(key, value);
    //}
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

  private final ConcurrentMap<String, RClusteredMap<Object, Object>> caches = new ConcurrentHashMap<String, RClusteredMap<Object, Object>>();

  private RClusteredMap<Object, Object> getCache(final String region) {
    RClusteredMap<Object, Object> cache = caches.get(region);
    if (cache == null) {
      cache = redisson.getClusteredMap(region);
      RClusteredMap<Object, Object> concurrent = caches.putIfAbsent(region, cache);
      if (concurrent != null) {
        cache = concurrent;
      }
    }
    return cache;
  }
}
