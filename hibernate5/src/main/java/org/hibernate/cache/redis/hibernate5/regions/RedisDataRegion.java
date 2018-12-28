/*
 * Copyright (c) 2017. Sunghyouk Bae <sunghyouk.bae@gmail.com>
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

package org.hibernate.cache.redis.hibernate5.regions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate5.ConfigurableRedisRegionFactory;
import org.hibernate.cache.redis.hibernate5.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.redis.util.CacheTimestamper;
import org.hibernate.cache.redis.util.RedisCacheUtil;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.Region;

import java.util.Map;
import java.util.Properties;

/**
 * RedisDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2015. 8. 27.
 */
@Slf4j
public abstract class RedisDataRegion implements Region {

  @Getter
  protected final RedisAccessStrategyFactory accessStrategyFactory;

  /**
   * Region regionName
   */
  private final String regionName;

  /**
   * Redis client instance deal hibernate data region.
   */
  @Getter
  protected final RedisClient redis;

  private final CacheTimestamper cacheTimestamper;

  @Getter
  private final int expiryInSeconds;  // seconds

  @Getter
  private final CacheKeysFactory keysFactory;

  public RedisDataRegion(RedisAccessStrategyFactory accessStrategyFactory,
      RedisClient redis, ConfigurableRedisRegionFactory configurableRedisRegionFactory,
      String regionName,
      Properties props, CacheKeysFactory cacheKeysFactory) {
    this.accessStrategyFactory = accessStrategyFactory;
    this.redis = redis;
    this.regionName = regionName;
    this.cacheTimestamper = configurableRedisRegionFactory.createCacheTimestamper(redis, regionName);
    this.expiryInSeconds = RedisCacheUtil.getExpiryInSeconds(this.regionName);
    this.keysFactory = cacheKeysFactory;
    log.debug("redis region={}, expiryInSeconds={}", regionName, expiryInSeconds);
  }

  /**
   * Region regionName
   *
   * @return region regionName
   */
  public String getName() {
    return regionName;
  }

  /**
   * delete region
   */
  @Override
  public void destroy() throws CacheException {
    // NOTE: No need to delete region in HA mode
  }

  /**
   * confirm the specified key exists in current region
   *
   * @param key cache key
   * @return if cache key is exists in current region return true, else return false
   */
  @Override
  public boolean contains(Object key) {
    try {
      log.debug("contains key={}", key);
      return redis.exists(regionName, key);
    } catch (Exception ignored) {
      log.warn("Fail to exists key. key=" + key, ignored);
      return false;
    }
  }

  @Override
  public long getSizeInMemory() {
    try {
      long sizeInMemory = redis.dbSize();

      log.trace("size in memory. region={}, size={}", regionName, sizeInMemory);
      return sizeInMemory;
    } catch (Exception ignored) {
      log.warn("Fail to get size in memory.", ignored);
      return 0;
    }
  }

  @Override
  public long getElementCountInMemory() {
    try {
      long elementCount = redis.keySizeInRegion(regionName);

      log.trace("region={}, elementCount={}", regionName, elementCount);
      return elementCount;
    } catch (Exception ignored) {
      log.warn("Fail to get element count in memory", ignored);
      return 0;
    }
  }

  @Override
  public long getElementCountOnDisk() {
    return -1;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map toMap() {
    return redis.getAll(regionName);
  }

  @Override
  public long nextTimestamp() {
    return cacheTimestamper.next();
  }

  @Override
  public int getTimeout() {
    return 0;
  }
}
