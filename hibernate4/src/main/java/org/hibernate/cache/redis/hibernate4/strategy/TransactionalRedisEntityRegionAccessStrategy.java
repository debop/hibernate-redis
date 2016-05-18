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

package org.hibernate.cache.redis.hibernate4.strategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate4.regions.RedisEntityRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * TransactionalRedisEntityRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalRedisEntityRegionAccessStrategy
    extends AbstractRedisAccessStrategy<RedisEntityRegion>
    implements EntityRegionAccessStrategy {

  @Getter
  private final RedisClient redis;

  public TransactionalRedisEntityRegionAccessStrategy(RedisEntityRegion region,
                                                      Settings settings) {
    super(region, settings);
    this.redis = region.getRedis();
  }

  @Override
  public EntityRegion getRegion() {
    return region;
  }

  @Override
  public Object get(Object key, long txTimestamp) {
    return region.get(key);
  }

  @Override
  public boolean putFromLoad(Object key,
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
  public SoftLock lockItem(Object key, Object version) {
    region.remove(key);
    return null;
  }

  @Override
  public void unlockItem(Object key, SoftLock lock) {
    region.remove(key);
  }

  @Override
  public boolean insert(Object key, Object value, Object version) {
    region.put(key, value);
    return true;
  }

  @Override
  public boolean afterInsert(Object key, Object value, Object version) {
    return false;
  }

  @Override
  public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) {
    region.put(key, value);
    return true;
  }

  @Override
  public boolean afterUpdate(Object key,
                             Object value,
                             Object currentVersion,
                             Object previousVersion,
                             SoftLock lock) {
    return false;
  }

  @Override
  public void remove(Object key) {
    region.remove(key);
  }
}
