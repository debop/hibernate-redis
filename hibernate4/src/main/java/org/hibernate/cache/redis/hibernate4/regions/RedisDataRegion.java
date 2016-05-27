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

package org.hibernate.cache.redis.hibernate4.regions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate4.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.redis.util.RedisCacheUtil;
import org.hibernate.cache.redis.util.Timestamper;
import org.hibernate.cache.spi.Region;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Base DataRegion using Redis
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:48
 */
@Slf4j
public abstract class RedisDataRegion implements Region {

  @Getter
  protected final RedisAccessStrategyFactory accessStrategyFactory;

  /**
   * Region name
   */
  private final String name;

  /**
   * Redis client instance deal hibernate data region.
   */
  @Getter
  protected final RedisClient redis;
  @Getter
  private final int cacheLockTimeout; // milliseconds
  @Getter
  private final int expiryInSeconds;  // seconds

  @Getter
  protected boolean regionDeleted = false;

  protected RedisDataRegion(RedisAccessStrategyFactory accessStrategyFactory,
                            RedisClient redis,
                            String regionName,
                            Properties props) {
    this.accessStrategyFactory = accessStrategyFactory;
    this.redis = redis;
    this.name = regionName;

    this.cacheLockTimeout = 0;
    this.expiryInSeconds = RedisCacheUtil.getExpiryInSeconds(name);
    log.debug("redis region={}, expiryInSeconds={}", regionName, expiryInSeconds);
  }

  /**
   * Region name
   *
   * @return region name
   */
  public String getName() {
    return name;
  }

  /**
   * delete region
   */
  @Override
  public void destroy() throws CacheException {
    // NOTE: HA 구성 시에는 region 을 삭제할 필요는 없습니다.
    // NOTE: 단 Cache 조회 시 expiration 되었는지 확인해야 합니다.
    log.info("destroy region... but not delete redis key. region=[{}]", name);
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
      boolean exists = redis.exists(name, key);
      log.trace("cache contains items? region=[{}], key=[{}], contains=[{}]", name, key, exists);
      return exists;
    } catch (Throwable ignored) {
      log.warn("Fail to check contains key... region=" + name, ignored);
      return false;
    }
  }

  @Override
  public long getSizeInMemory() {
    try {
      return redis.dbSize();
    } catch (Throwable ignored) {
      log.warn("Fail to get count of cache items. region=" + name, ignored);
      return -1;
    }
  }

  @Override
  public long getElementCountInMemory() {
    try {
      return redis.keySizeInRegion(name);
    } catch (Throwable ignored) {
      log.warn("Fail to get count of cache items. region=" + name, ignored);
      return -1;
    }
  }

  @Override
  public long getElementCountOnDisk() {
    return -1;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map toMap() {
    try {
      return redis.getAll(name);
    } catch (Throwable e) {
      log.warn("Fail to build CacheEntry. return EmptyMap.", e);
      return Collections.emptyMap();
    }
  }

  @Override
  public long nextTimestamp() {
    return Timestamper.next();
  }

  @Override
  public int getTimeout() {
    return cacheLockTimeout;
  }
}
