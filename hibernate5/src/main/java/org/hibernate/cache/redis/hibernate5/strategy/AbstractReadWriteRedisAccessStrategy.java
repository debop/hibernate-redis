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
import org.hibernate.cache.redis.hibernate5.regions.RedisTransactionalDataRegion;
import org.hibernate.engine.spi.SessionImplementor;

import java.util.Comparator;

/**
 * Superclass for all Redis specific read/write AccessStrategy implementations.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2015. 8. 28.
 */
@Slf4j
public abstract class AbstractReadWriteRedisAccessStrategy<T extends RedisTransactionalDataRegion>
    extends AbstractRedisAccessStrategy<T> {

  private final Comparator versionComparator;

  public AbstractReadWriteRedisAccessStrategy(T region, SessionFactoryOptions options) {
    super(region, options);
    this.versionComparator = region.getCacheDataDescription().getVersionComparator();
  }


  public boolean putFromLoad(SessionImplementor session,
                             Object key,
                             Object value,
                             long txTimestamp,
                             Object version,
                             boolean minimalPutOverride) {
    region.put(key, value);
    return true;
  }


}
