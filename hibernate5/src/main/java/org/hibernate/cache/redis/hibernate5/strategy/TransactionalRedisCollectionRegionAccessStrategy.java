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

package org.hibernate.cache.redis.hibernate5.strategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate5.regions.RedisCollectionRegion;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.CollectionPersister;

/**
 * JTA CollectionRegionAccessStrategy.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalRedisCollectionRegionAccessStrategy
    extends AbstractRedisAccessStrategy<RedisCollectionRegion>
    implements CollectionRegionAccessStrategy {

  @Getter
  private final RedisClient redis;

  public TransactionalRedisCollectionRegionAccessStrategy(RedisCollectionRegion region,
                                                          SessionFactoryOptions options) {
    super(region, options);
    this.redis = region.getRedis();
  }

  @Override
  public Object generateCacheKey(Object id,
                                 CollectionPersister persister,
                                 SessionFactoryImplementor factory,
                                 String tenantIdentifier) {
    return region.getKeysFactory().createCollectionKey(id, persister, factory, tenantIdentifier);
  }

  @Override
  public Object getCacheKeyId(Object cacheKey) {
    return region.getKeysFactory().getCollectionId(cacheKey);
  }

  @Override
  public CollectionRegion getRegion() {
    return region;
  }

  @Override
  public boolean putFromLoad(SessionImplementor session,
                             Object key,
                             Object value,
                             long txTimestamp,
                             Object version,
                             boolean minimalPutOverride) {
    if (minimalPutOverride && region.contains(key)) {
      return false;
    }
    region.put(key, value);
    return true;
  }

  @Override
  public SoftLock lockItem(SessionImplementor session, Object key, Object version) {
    region.remove(key);
    return null;
  }

  @Override
  public void unlockItem(SessionImplementor session, Object key, SoftLock lock) {
    region.remove(key);
  }

  @Override
  public void remove(SessionImplementor session, Object key) {
    region.remove(key);
  }
}
