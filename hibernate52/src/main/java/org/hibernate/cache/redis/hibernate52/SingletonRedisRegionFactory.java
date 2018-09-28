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

package org.hibernate.cache.redis.hibernate52;

import static java.util.Objects.nonNull;
import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.util.RedisCacheUtil;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton Hibernate 5.2.x or higher 2nd Cache Region Factory using Redis
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2015. 8. 28.
 */
@Slf4j
public class SingletonRedisRegionFactory extends AbstractRedisRegionFactory {

  private final AtomicInteger referenceCount = new AtomicInteger();

  public SingletonRedisRegionFactory(@NonNull Properties props) {
    super(props);
    log.info("create SingletonRedisRegionFactory instance.");
  }

  @Override
  @Synchronized
  public void start(SessionFactoryOptions options, Properties properties) throws CacheException {
    log.debug("SingletonRedisRegionFactory is starting... options={}, properties={}",
              options, properties);

    this.options = options;
    try {
      if (redis == null) {
        RedisCacheUtil.loadCacheProperties(properties);
        this.redis = (nonNull(RedisCacheUtil.getRedissonJavaConfig())) ?
                createRedisClient(RedisCacheUtil.getRedissonJavaConfig()) :
                createRedisClient();
	      this.cacheTimestamper = createCacheTimestamper(redis, SingletonRedisRegionFactory.class.getName());
	      RedisCacheUtil.saveRedisSingletonClient(this.redis);
      }
      if (redis != null)
        referenceCount.incrementAndGet();
      log.info("RedisRegionFactory is started.");
    } catch (Exception e) {
      throw new CacheException(e);
    }
  }

  @Override
  @Synchronized
  public void stop() {
    if (this.redis == null)
      return;

    if (referenceCount.decrementAndGet() == 0) {
      log.debug("RedisRegionFactory is stopping...");
      try {
        redis.shutdown();
        redis = null;
        cacheTimestamper = null;
        log.info("RedisRegionFactory is stopped.");
      } catch (Exception ignored) {
        log.error("Fail to stop SingletonRedisRegionFactory.", ignored);
      }
    }
  }

  private static final long serialVersionUID = 1858232236910287076L;
}
