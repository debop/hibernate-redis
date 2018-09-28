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

package org.hibernate.cache.redis.hibernate52.regions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate52.ConfigurableRedisRegionFactory;
import org.hibernate.cache.redis.hibernate52.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cache.spi.CacheKeysFactory;
import java.util.Properties;

/**
 * RedisTransactionalDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 */
@Slf4j
@Getter
public class RedisTransactionalDataRegion extends RedisDataRegion implements TransactionalDataRegion {

  /**
   * Aggregator of special options used to build the SessionFactory.
   */
  protected final SessionFactoryOptions options;
  /**
   * Metadata associated with the objects sorted in the region
   */
  protected final CacheDataDescription metadata;

  public RedisTransactionalDataRegion(RedisAccessStrategyFactory accessStrategyFactory,
                                      RedisClient redis, ConfigurableRedisRegionFactory configurableRedisRegionFactory,
                                      String regionName,
                                      SessionFactoryOptions options,
                                      CacheDataDescription metadata,
                                      Properties props, CacheKeysFactory cacheKeysFactory) {
    super(accessStrategyFactory, redis, configurableRedisRegionFactory, regionName, props, cacheKeysFactory);

    this.options = options;
    this.metadata = metadata;
  }

  @Override
  public boolean isTransactionAware() {
    return false;
  }

  @Override
  public CacheDataDescription getCacheDataDescription() {
    return metadata;
  }

  public Object get(Object key) {
    try {
      return redis.get(getName(), key);
    } catch (Exception ignored) {
      log.warn("Fail to get cache item... key=" + key, ignored);
      return null;
    }
  }

  public void put(Object key, Object value) {
    try {
      redis.set(getName(), key, value, getExpiryInSeconds());
    } catch (Exception ignored) {
      log.warn("Fail to put cache item... key=" + key, ignored);
    }
  }

  public void remove(Object key) throws CacheException {
    try {
      redis.del(getName(), key);
    } catch (Exception ignored) {
      log.warn("Fail to remove cache item... key=" + key, ignored);
    }
  }

  public void clear() {
    try {
      redis.deleteRegion(getName());
    } catch (Exception ignored) {
      log.warn("Fail to clear region... name=" + getName(), ignored);
    }
  }
}
