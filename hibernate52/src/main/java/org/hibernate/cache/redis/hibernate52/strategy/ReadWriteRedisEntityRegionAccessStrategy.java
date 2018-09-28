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

package org.hibernate.cache.redis.hibernate52.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.redis.hibernate52.regions.RedisEntityRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

/**
 * ReadWriteRedisEntityRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:13
 */
@Slf4j
public class ReadWriteRedisEntityRegionAccessStrategy
    extends AbstractReadWriteRedisAccessStrategy<RedisEntityRegion>
    implements EntityRegionAccessStrategy {

  /**
   * Creates a read/write cache access strategy around the given cache region.
   */
  public ReadWriteRedisEntityRegionAccessStrategy(RedisEntityRegion region,
                                                  SessionFactoryOptions options) {
    super(region, options);
  }

  @Override
  public Object generateCacheKey(Object id,
                                 EntityPersister persister,
                                 SessionFactoryImplementor factory,
                                 String tenantIdentifier) {
    return region.getCacheKeysFactory().createEntityKey(id, persister, factory, tenantIdentifier);
  }

  @Override
  public Object getCacheKeyId(Object cacheKey) {
    return region.getCacheKeysFactory().getEntityId(cacheKey);
  }

  @Override
  public EntityRegion getRegion() {
    return region;
  }

  @Override
  public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
    region.put(key, value);
    return true;
  }

  @Override
  public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) {
    region.put(key, value);
    return true;
  }

  @Override
  public boolean update(SharedSessionContractImplementor session,
                        Object key,
                        Object value,
                        Object currentVersion,
                        Object previousVersion) {
    region.put(key, value);
    return true;
  }

  @Override
  public boolean afterUpdate(SharedSessionContractImplementor session,
                             Object key,
                             Object value,
                             Object currentVersion,
                             Object previousVersion,
                             SoftLock lock) {
    region.put(key, value);
    return true;
  }
}
