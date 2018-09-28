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
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate52.ConfigurableRedisRegionFactory;
import org.hibernate.cache.redis.hibernate52.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.CacheKeysFactory;
import java.util.Properties;

/**
 * RedisNaturalIdRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2015. 8. 27.
 */
@Slf4j
public class RedisNaturalIdRegion extends RedisTransactionalDataRegion implements NaturalIdRegion {

  public RedisNaturalIdRegion(RedisAccessStrategyFactory accessStrategyFactory,
                              RedisClient redis, ConfigurableRedisRegionFactory configurableRedisRegionFactory,
                              String regionName,
                              SessionFactoryOptions options,
                              CacheDataDescription metadata,
                              Properties props, CacheKeysFactory cacheKeysFactory) {
    super(accessStrategyFactory, redis, configurableRedisRegionFactory, regionName, options, metadata, props, cacheKeysFactory);
  }

  @Override
  public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
    return accessStrategyFactory.createNaturalIdRegionAccessStrategy(this, accessType);
  }
}
