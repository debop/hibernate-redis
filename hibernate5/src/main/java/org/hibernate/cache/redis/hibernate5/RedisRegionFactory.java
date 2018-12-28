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

package org.hibernate.cache.redis.hibernate5;

import java.util.Properties;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.util.RedisCacheUtil;

/**
 * Hibernate 5.x 2nd Cache Region Factory using Redis
 *
 * @author sunghyouk.bae@gmail.com
 */
@Slf4j
public class RedisRegionFactory extends AbstractRedisRegionFactory {

  public RedisRegionFactory(Properties props) {
    super(props);
  }

  @Override
  @Synchronized
  public void start(SessionFactoryOptions options, Properties properties) throws CacheException {
    log.debug("RedisRegionFactory is starting... options={}, properties={}", options, properties);

    this.options = options;
    try {
      if (redis == null) {
        RedisCacheUtil.loadCacheProperties(props);
        this.redis = (RedisCacheUtil.getRedissonJavaConfig() != null) ?
            createRedisClient(RedisCacheUtil.getRedissonJavaConfig()) :
            createRedisClient();
        this.cacheTimestamper = createCacheTimestamper(redis, RedisRegionFactory.class.getName());
      }
      log.info("RedisRegionFactory is started.");
    } catch (Exception e) {
      log.error("Fail to start RedisRegionFactory.", e);
      throw new CacheException(e);
    }
  }

  @Override
  @Synchronized
  public void stop() {
    if (redis == null)
      return;
    log.debug("Stopping RedisRegionFactory...");

    try {
      redis.shutdown();
      redis = null;
      cacheTimestamper = null;
      log.info("RedisRegionFactory is stopped.");
    } catch (Exception ignored) {
      log.error("Fail to stop RedisRegionFactory.", ignored);
    }
  }

  private static final long serialVersionUID = -6772837841732399223L;
}
