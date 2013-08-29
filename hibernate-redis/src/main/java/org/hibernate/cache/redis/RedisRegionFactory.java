/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cache.redis.util.JedisTool;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * A non-singleton RedisRegionFactory implementation.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:41
 */
public class RedisRegionFactory extends AbstractRedisRegionFactory {

    private static final Logger log = LoggerFactory.getLogger(RedisRegionFactory.class);
    private static final boolean isTraceEnabled = log.isTraceEnabled();
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    private JedisClient jedisClient;

    public RedisRegionFactory(Properties props) {
        super(props);
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        log.info("Redis를 2차 캐시 저장소로 사용하는 RedisRegionFactory를 시작합니다...");

        this.settings = settings;
        try {
            this.jedisClient = JedisTool.createJedisClient(props);
            log.info("RedisRegionFactory를 시작했습니다...");
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void stop() {
        if (jedisClient == null) return;
        log.debug("RedisRegionFactory 사용을 중지합니다...");

        try {
            jedisClient.flushDb();
            jedisClient = null;
            log.info("RedisRegionFactory를 중지했습니다.");
        } catch (Exception e) {
            log.error("jedisClient region factory fail to stop.", e);
            throw new CacheException(e);
        }
    }
}
