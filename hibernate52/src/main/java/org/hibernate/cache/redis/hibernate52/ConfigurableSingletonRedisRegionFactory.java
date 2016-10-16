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

package org.hibernate.cache.redis.hibernate52;

import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClientFactory;
import org.hibernate.cache.redis.config.RedissonConfigFactory;
import org.hibernate.cache.redis.config.SingleRedissonConfigFactory;

import java.util.Properties;

/**
 * ConfigurableSingletonRedisRegionFactory
 *
 * @author sunghyouk.bae@gmail.com
 */
@Slf4j
public class ConfigurableSingletonRedisRegionFactory extends SingletonRedisRegionFactory {

  public ConfigurableSingletonRedisRegionFactory(@NonNull Properties props) {
    super(props);
  }

  @Override
  @Synchronized
  protected void setupFromRedissonConfig() {
    if (redis == null) {
      RedissonConfigFactory configFactory = new SingleRedissonConfigFactory();
      redis = RedisClientFactory.createRedisClient(configFactory.create());
    }
  }

  @Override
  @Synchronized
  public void start(SessionFactoryOptions options, Properties properties) throws CacheException {
    log.debug("SingletonRedisRegionFactory is strting... optinos={}, properties={}",
              options, properties);

    this.options = options;
    try {
      if (redis == null) {
        setupFromRedissonConfig();
      }
      if (redis != null)
        referenceCount.incrementAndGet();
      log.info("RedisRegionFactory is started.");
    } catch (Exception e) {
      throw new CacheException(e);
    }
  }
}
