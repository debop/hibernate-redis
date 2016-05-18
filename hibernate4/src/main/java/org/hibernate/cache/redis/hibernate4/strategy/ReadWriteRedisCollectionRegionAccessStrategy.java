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

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.hibernate4.regions.RedisCollectionRegion;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

/**
 * ReadWriteRedisCollectionRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:13
 */
@Slf4j
public class ReadWriteRedisCollectionRegionAccessStrategy
    extends AbstractReadWriteRedisAccessStrategy<RedisCollectionRegion>
    implements CollectionRegionAccessStrategy {

  public ReadWriteRedisCollectionRegionAccessStrategy(RedisCollectionRegion region, Settings settings) {
    super(region, settings);
  }

  @Override
  public CollectionRegion getRegion() {
    return region;
  }
}
