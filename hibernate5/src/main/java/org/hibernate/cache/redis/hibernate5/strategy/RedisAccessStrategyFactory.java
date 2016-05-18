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

package org.hibernate.cache.redis.hibernate5.strategy;

import org.hibernate.cache.redis.hibernate5.regions.RedisCollectionRegion;
import org.hibernate.cache.redis.hibernate5.regions.RedisEntityRegion;
import org.hibernate.cache.redis.hibernate5.regions.RedisNaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * RedisAccessStrategyFactory
 *
 * @author sunghyouk.bae@gmail.com
 */
public interface RedisAccessStrategyFactory {

  /**
   * Create {@link EntityRegionAccessStrategy}
   * for the input {@link org.hibernate.cache.redis.hibernate5.regions.RedisEntityRegion} and {@link AccessType}
   *
   * @return {@link EntityRegionAccessStrategy} instance.
   */
  EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion entityRegion,
                                                              AccessType accessType);

  /**
   * Create {@link CollectionRegionAccessStrategy}
   * for the input {@link RedisCollectionRegion} and {@link AccessType}
   *
   * @return {@link RedisCollectionRegion} instance.
   */
  CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(RedisCollectionRegion collectionRegion,
                                                                      AccessType accessType);

  /**
   * Create {@link CollectionRegionAccessStrategy}
   * for the input {@link RedisNaturalIdRegion} and {@link AccessType}
   *
   * @return {@link RedisNaturalIdRegion} instance.
   */
  NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(RedisNaturalIdRegion naturalIdRegion,
                                                                    AccessType accessType);

}
