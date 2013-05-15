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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A singleton RedisRegionFactory implementation.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:31
 */
public class SingletonRedisRegionFactory extends AbstractRedisRegionFactory {

    private static final Logger log = LoggerFactory.getLogger(SingletonRedisRegionFactory.class);
    private static final boolean isTraceEnabled = log.isTraceEnabled();
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    private static final AtomicInteger ReferenceCount = new AtomicInteger();

    private JedisClient jedisClient;

    public SingletonRedisRegionFactory(Properties props) {
        super(props);
        this.jedisClient = JedisTool.createJedisClient(props);
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        if (isDebugEnabled)
            log.debug("Starting region factory...");

        this.settings = settings;

        try {
            this.jedisClient = JedisTool.createJedisClient(props);
            ReferenceCount.incrementAndGet();

            if (isDebugEnabled)
                log.debug("Start region factory");
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void stop() {
        if (isDebugEnabled)
            log.debug("Stop regoin factory...");

        try {
            if (ReferenceCount.decrementAndGet() == 0) {
                jedisClient.flushDb();

                if (isDebugEnabled)
                    log.debug("flush db");
            }
            log.debug("Stop region factory is success");
            jedisClient = null;
        } catch (Exception e) {
            log.error("jedisClient region factory fail to stop.", e);
        }
    }
}
