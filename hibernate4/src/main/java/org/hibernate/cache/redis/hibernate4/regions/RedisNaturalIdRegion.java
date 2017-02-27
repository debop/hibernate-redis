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

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate4.ConfigurableRedisRegionFactory;
import org.hibernate.cache.redis.hibernate4.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * An naturalId region specific wrapper around an Redis.
 * <p/>
 * This implementation returns Redis specific access strategy instances for all the non-transactional access types.
 * Transactional access is not supported.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:55
 */
public class RedisNaturalIdRegion extends RedisTransactionalDataRegion implements NaturalIdRegion {

  public RedisNaturalIdRegion(RedisAccessStrategyFactory accessStrategyFactory,
                              RedisClient redis, ConfigurableRedisRegionFactory configurableRedisRegionFactory,
                              String regionName,
                              Settings settings,
                              CacheDataDescription metadata,
                              Properties props) {
    super(accessStrategyFactory, redis, configurableRedisRegionFactory, regionName, settings, metadata, props);
  }

  @Override
  public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
    return getAccessStrategyFactory().createNaturalIdRegionAccessStrategy(this, accessType);
  }
}
