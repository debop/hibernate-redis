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

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate52.ConfigurableRedisRegionFactory;
import org.hibernate.cache.redis.hibernate52.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.cache.spi.CacheKeysFactory;
import java.util.Properties;

/**
 * RedisGeneralDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2015. 8. 27.
 */
@Slf4j
public class RedisGeneralDataRegion extends RedisDataRegion implements GeneralDataRegion {

  public RedisGeneralDataRegion(RedisAccessStrategyFactory accessStrategyFactory,
                                RedisClient redis, ConfigurableRedisRegionFactory configurableRedisRegionFactory,
                                String regionName,
                                Properties props, CacheKeysFactory cacheKeysFactory) {
    super(accessStrategyFactory, redis, configurableRedisRegionFactory, regionName, props, cacheKeysFactory);
  }

  @Override
  public Object get(SharedSessionContractImplementor session, Object key) throws CacheException {
    try {
      return redis.get(getName(), key);
    } catch (Exception ignored) {
      return null;
    }
  }

  @Override
  public void put(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
    try {
      redis.set(getName(), key, value, getExpiryInSeconds());
    } catch (Exception ignored) {
      log.warn("Fail to put. key=" + key, ignored);
    }
  }

  @Override
  public void evict(Object key) throws CacheException {
    try {
      redis.del(getName(), key);
    } catch (Exception ignored) {
      log.warn("Fail to evict. key=" + key, ignored);
    }
  }

  @Override
  public void evictAll() throws CacheException {
    try {
      redis.deleteRegion(getName());
    } catch (Exception ignored) {
      log.warn("Fail to evict all.", ignored);
    }
  }
}
