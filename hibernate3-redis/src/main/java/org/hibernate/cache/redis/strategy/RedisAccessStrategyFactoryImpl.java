/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.regions.RedisCollectionRegion;
import org.hibernate.cache.redis.regions.RedisEntityRegion;
import org.hibernate.cache.access.AccessType;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.access.EntityRegionAccessStrategy;

/**
 * org.hibernate.cache.redis.strategy.RedisAccessStrategyFactoryImpl
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:03
 */
@Slf4j
public class RedisAccessStrategyFactoryImpl implements RedisAccessStrategyFactory {

    @Override
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion entityRegion,
                                                                       AccessType accessType) {
        log.debug("create EntityRegionAccessStrategy. regionName=[{}], accessType=[{}]",
                      entityRegion.getName(), accessType.getName());

        if (AccessType.READ_ONLY == accessType) {
            if (entityRegion.getCacheDataDescription().isMutable()) {
                log.warn("read-only cache configured for mutable entity regionName=[{}]", entityRegion.getName());
            }
            return new ReadOnlyRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
        } else if (AccessType.READ_WRITE == accessType) {
            return new ReadWriteRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
        } else if (AccessType.NONSTRICT_READ_WRITE == accessType) {
            return new NonStrictReadWriteRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
        } else if (AccessType.TRANSACTIONAL == accessType) {
            return new TransactionalRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
        } else {
            throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    @Override
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(RedisCollectionRegion collectionRegion,
                                                                               AccessType accessType) {
        log.debug("create CollectionRegionAccessStrategy. regionName=[{}], accessType=[{}]",
                      collectionRegion.getName(), accessType.getName());

        if (AccessType.READ_ONLY == accessType) {
            if (collectionRegion.getCacheDataDescription().isMutable()) {
                log.warn("read-only cache configured for mutable entity collectionRegionName=[{}]", collectionRegion.getName());
            }
            return new ReadOnlyRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
        } else if (AccessType.READ_WRITE == accessType) {
            return new ReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
        } else if (AccessType.NONSTRICT_READ_WRITE == accessType) {
            return new NonStrictReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
        } else if (AccessType.TRANSACTIONAL == accessType) {
            return new TransactionalRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
        } else {
            throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }
}
