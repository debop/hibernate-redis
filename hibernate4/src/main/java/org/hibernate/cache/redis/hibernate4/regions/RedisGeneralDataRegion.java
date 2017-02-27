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
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate4.ConfigurableRedisRegionFactory;
import org.hibernate.cache.redis.hibernate4.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.spi.GeneralDataRegion;

import java.util.Properties;

/**
 * RedisGeneralDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 9:00
 */
@Slf4j
public abstract class RedisGeneralDataRegion extends RedisDataRegion implements GeneralDataRegion {

  protected RedisGeneralDataRegion(RedisAccessStrategyFactory accessStrategyFactory,
                                   RedisClient redis, ConfigurableRedisRegionFactory configurableRedisRegionFactory,
                                   String regionName,
                                   Properties props) {
    super(accessStrategyFactory, redis, configurableRedisRegionFactory, regionName, props);
  }

  @Override
  public Object get(Object key) {
    if (key == null) return null;
    try {
      return redis.get(getName(), key);
    } catch (Exception e) {
      log.warn("Fail to get cache item... key=" + key, e);
      return null;
    }
  }

  @Override
  public void put(Object key, Object value) {
    try {
      redis.set(getName(), key, value, getExpiryInSeconds());
    } catch (Exception e) {
      log.warn("Fail to put cache item... key=" + key, e);
    }
  }

  @Override
  public void evict(Object key) {
    try {
      redis.del(getName(), key);
    } catch (Exception e) {
      log.warn("Fail to remove cache item... key=" + key, e);
    }
  }

  @Override
  public void evictAll() {
    try {
      redis.deleteRegion(getName());
    } catch (Exception e) {
      log.warn("Fail to remove cache items... region=" + getName(), e);
    }
  }
}
