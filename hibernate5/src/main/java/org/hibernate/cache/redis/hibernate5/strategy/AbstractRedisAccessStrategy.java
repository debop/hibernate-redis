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

import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.hibernate5.regions.RedisTransactionalDataRegion;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionImplementor;

/**
 * AbstractRedisAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2015. 8. 28.
 */
@Slf4j
abstract class AbstractRedisAccessStrategy<T extends RedisTransactionalDataRegion> {

  protected final T region;
  protected final SessionFactoryOptions options;

  AbstractRedisAccessStrategy(T region, SessionFactoryOptions options) {
    this.region = region;
    this.options = options;
  }

  protected SessionFactoryOptions options() {
    return this.options;
  }

  /**
   * Returns <code>null</code> if the item is not readable.  Locked items are not readable, nor are items created
   * after the start of this transaction.
   */
  public Object get(SessionImplementor session, Object key, long txTimestamp) {
    return region.get(key);
  }

  public final boolean putFromLoad(SessionImplementor session,
                                   Object key,
                                   Object value,
                                   long txTimestamp,
                                   Object version) {
    return putFromLoad(session, key, value, txTimestamp, version, options.isMinimalPutsEnabled());
  }

  abstract boolean putFromLoad(SessionImplementor session,
                               Object key,
                               Object value,
                               long txTimestamp,
                               Object version,
                               boolean minimalPutOverride) throws CacheException;

  /**
   * Region locks are not supported
   */
  public final SoftLock lockRegion() {
    return null;
  }

  /**
   * Region locks are not supported - perform a cache clear as a precaution.
   *
   * @param lock soft lock instance
   */
  public final void unlockRegion(SoftLock lock) {
    region.clear();
  }

  /**
   * A no-op since this is an asynchronous cache access strategy.
   *
   * @param key key
   */
  public void remove(SessionImplementor session, Object key) throws CacheException {
    region.remove(key);
  }

  /**
   * Called to evict data from the entire region
   */
  public final void removeAll() {
    region.clear();
  }

  /**
   * Remove the given mapping without regard to transactional safety
   *
   * @param key key
   */
  public final void evict(Object key) {
    region.remove(key);
  }

  /**
   * Remove all mappings without regard to transactional safety
   */
  public final void evictAll() {
    region.clear();
  }

  /**
   * Soft-lock a cache item.
   */
  public SoftLock lockItem(SessionImplementor session, Object key, Object version) throws CacheException {
    region.remove(key);
    return null;
  }

  /**
   * Soft-unlock a cache item.
   */
  public void unlockItem(SessionImplementor session, Object key, SoftLock lock) throws CacheException {
    region.remove(key);
  }
}
