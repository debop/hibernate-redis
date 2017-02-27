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

package org.hibernate.cache.redis.hibernate4.regions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate4.ConfigurableRedisRegionFactory;
import org.hibernate.cache.redis.hibernate4.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * RedisTransactionalDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 9:44
 */
@Slf4j
public class RedisTransactionalDataRegion extends RedisDataRegion implements TransactionalDataRegion {

  /**
   * Hibernate settings associated with the persistence unit.
   */
  protected final Settings settings;
  /**
   * Metadata associated with the objects sorted in the region
   */
  protected final CacheDataDescription metadata;

  public RedisTransactionalDataRegion(RedisAccessStrategyFactory accessStrategyFactory,
                                      RedisClient redis, ConfigurableRedisRegionFactory configurableRedisRegionFactory,
                                      String regionName,
                                      Settings settings,
                                      CacheDataDescription metadata,
                                      Properties props) {
    super(accessStrategyFactory, redis, configurableRedisRegionFactory, regionName, props);

    this.settings = settings;
    this.metadata = metadata;
  }

  public Settings getSettings() {
    return settings;
  }

  @Override
  public boolean isTransactionAware() {
    return false;
  }

  @Override
  public CacheDataDescription getCacheDataDescription() {
    return metadata;
  }

  public <T> T get(Object key) {
    try {
      return redis.get(getName(), key);
    } catch (Exception e) {
      log.warn("Fail to get cache item... key=" + key, e);
      return null;
    }
  }


  public void put(Object key, Object value) {
    try {
      redis.set(getName(), key, value, getExpiryInSeconds());
    } catch (Exception e) {
      log.warn("Fail to put cache item... key=" + key, e);
    }
  }

  public void remove(Object key) throws CacheException {
    try {
      redis.del(getName(), key);
    } catch (Exception e) {
      log.warn("Fail to remove cache item... key=" + key, e);
    }
  }


  public void clear() {
    try {
      redis.deleteRegion(getName());
    } catch (Exception e) {
      log.warn("Fail to clear region... name=" + getName(), e);
    }
  }
}
