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

package org.hibernate.cache.redis.hibernate52.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.hibernate52.regions.RedisCollectionRegion;
import org.hibernate.cache.redis.hibernate52.regions.RedisEntityRegion;
import org.hibernate.cache.redis.hibernate52.regions.RedisNaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * RedisAccessStrategyFactoryImpl
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2015. 8. 28.
 */
@Slf4j
public class RedisAccessStrategyFactoryImpl implements RedisAccessStrategyFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion entityRegion,
                                                                     AccessType accessType) {
    switch (accessType) {
      case READ_ONLY:
        if (entityRegion.getCacheDataDescription().isMutable()) {
          log.warn("read-only cache configured for mutable entity regionName=[{}]", entityRegion.getName());
        }
        return new ReadOnlyRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getOptions());
      case READ_WRITE:
        return new ReadWriteRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getOptions());
      case NONSTRICT_READ_WRITE:
        return new NonStrictReadWriteRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getOptions());
      case TRANSACTIONAL:
        return new TransactionalRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getOptions());
      default:
        throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(RedisCollectionRegion collectionRegion,
                                                                             AccessType accessType) {
    switch (accessType) {
      case READ_ONLY:
        if (collectionRegion.getCacheDataDescription().isMutable()) {
          log.warn("read-only cache configured for mutable entity collectionRegionName=[{}]", collectionRegion.getName());
        }
        return new ReadOnlyRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getOptions());
      case READ_WRITE:
        return new ReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getOptions());
      case NONSTRICT_READ_WRITE:
        return new NonStrictReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getOptions());
      case TRANSACTIONAL:
        return new TransactionalRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getOptions());
      default:
        throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
    }
  }

  /**
   * {@inheritDoc}
   */
  public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(RedisNaturalIdRegion naturalIdRegion,
                                                                           AccessType accessType) {
    switch (accessType) {
      case READ_ONLY:
        if (naturalIdRegion.getCacheDataDescription().isMutable()) {
          log.warn("read-only cache configured for mutable entity naturalIdRegion=[{}]", naturalIdRegion.getName());
        }
        return new ReadOnlyRedisNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getOptions());
      case READ_WRITE:
        return new ReadWriteRedisNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getOptions());
      case NONSTRICT_READ_WRITE:
        return new NonStrictReadWriteRedisNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getOptions());
      case TRANSACTIONAL:
        return new TransactionalRedisNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getOptions());
      default:
        throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
    }
  }
}
