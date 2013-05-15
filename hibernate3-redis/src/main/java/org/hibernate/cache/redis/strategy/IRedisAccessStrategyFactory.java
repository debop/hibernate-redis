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

import org.hibernate.cache.redis.regions.RedisCollectionRegion;
import org.hibernate.cache.redis.regions.RedisEntityRegion;
import org.hibernate.cache.access.AccessType;
import org.hibernate.cache.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.access.EntityRegionAccessStrategy;

/**
 * IRedisAccessStrategyFactory
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:50
 */
public interface IRedisAccessStrategyFactory {

    /**
     * Create {@link EntityRegionAccessStrategy}
     * for the input {@link org.hibernate.cache.redis.regions.RedisEntityRegion} and {@link AccessType}
     *
     * @return the created {@link EntityRegionAccessStrategy}
     */
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion entityRegion, AccessType accessType);

    /**
     * Create {@link CollectionRegionAccessStrategy}
     * for the input {@link org.hibernate.cache.redis.regions.RedisCollectionRegion} and {@link AccessType}
     *
     * @return the created {@link org.hibernate.cache.redis.regions.RedisCollectionRegion}
     */
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(RedisCollectionRegion collectionRegion,
                                                                               AccessType accessType);
}
