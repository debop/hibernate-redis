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

package org.hibernate.cache.redis.hibernate5;

import lombok.NonNull;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClientFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton Hibernate 2nd Cache Region Factory using Redis
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
    log.info("Redis를 2차 캐시 저장소로 사용하는 RedisRegionFactory를 시작합니다...");

    this.options = options;
    try {
      if (redis == null) {
        this.redis = RedisClientFactory.createRedisClient(properties);
      }
      referenceCount.incrementAndGet();
      log.info("RedisRegionFactory를 시작합니다.");
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
      log.trace("RedisRegionFactory를 중지합니다...");
      try {
        redis.shutdown();
        redis = null;
        log.info("RedisRegionFactory를 중지했습니다.");
      } catch (Exception ignored) {
        log.warn("Fail to stop SingletonRedisRegionFactory.", ignored);
      }
    }
  }

  private static final long serialVersionUID = 1858232236910287076L;
}
