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

package org.hibernate.cache.redis.strategy;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.redis.hibernate52.regions.RedisTransactionalDataRegion;
import org.hibernate.cache.redis.hibernate52.strategy.AbstractReadWriteRedisAccessStrategy;

public class ItemValueExtractor extends AbstractReadWriteRedisAccessStrategy<RedisTransactionalDataRegion> {

  public ItemValueExtractor(RedisTransactionalDataRegion region, SessionFactoryOptions options) {
    super(region, options);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getValue(final Object entry) {
    return (T) entry;
  }
}
