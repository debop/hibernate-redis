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

package org.hibernate.cache.redis.regions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.redis.util.JedisTool;
import org.hibernate.cache.redis.util.Timestamper;
import org.hibernate.cache.spi.Region;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Base DataRegion using Redis
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:48
 */
@Slf4j
public abstract class RedisDataRegion implements Region {

    private static final String CACHE_LOCK_TIMEOUT_PROPERTY = "io.redis.hibernate.cache_lock_timeout";
    private static final int DEFAULT_CACHE_LOCK_TIMEOUT = 60 * 1000; // 60 seconds

    private static final String EXPIRE_IN_SECONDS = "redis.expiryInSeconds";

    @Getter
    protected final RedisAccessStrategyFactory accessStrategyFactory;

    /**
     * Region name
     */
    private final String name;

    /**
     * Redis client instance deal hibernate data region.
     */
    @Getter
    protected final JedisClient jedisClient;

    @Getter
    private final int cacheLockTimeout; // milliseconds

    @Getter
    private final int expireInSeconds;  // seconds

    @Getter
    protected boolean regionDeleted = false;

    protected RedisDataRegion(RedisAccessStrategyFactory accessStrategyFactory,
                              JedisClient jedisClient,
                              String regionName,
                              Properties props) {
        this.accessStrategyFactory = accessStrategyFactory;
        this.jedisClient = jedisClient;
        this.name = regionName;

        this.cacheLockTimeout =
                Integer.decode(props.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY,
                                                 String.valueOf(DEFAULT_CACHE_LOCK_TIMEOUT)));

        int defaultExpires = Integer.decode(JedisTool.getProperty(EXPIRE_IN_SECONDS, "120"));
        this.expireInSeconds = JedisTool.getExpireInSeconds(name, defaultExpires);
    }

    /**
     * Region name
     *
     * @return region name
     */
    public String getName() {

        return name;
    }

    /**
     * delete region
     *
     * @throws org.hibernate.cache.CacheException
     */
    @Override
    public void destroy() throws CacheException {
        // NOTE: delete all cached item is not neccessory.
        // NOTE: if you use multi servers, you don't need clear cache.
//        try {
//            if (!regionDeleted) {
//                jedisClient.deleteRegion(name);
//                regionDeleted = true;
//                log.debug("region[{}] is deleted.", getName());
//            }
//        } catch (Exception ignored) {}
    }

    /**
     * confirm the specified key exists in current region
     *
     * @param key cache key
     * @return if cache key is exists in current region return true, else return false
     */
    @Override
    public boolean contains(Object key) {
        try {
            return jedisClient.exists(name, key);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getSizeInMemory() {
        try {
            return jedisClient.dbSize();
        } catch (Throwable t) {
            log.warn("error", t);
            return -1;
        }
    }

    @Override
    public long getElementCountInMemory() {
        try {
            return jedisClient.keysInRegion(name).size();
        } catch (Exception e) {
            log.warn("error", e);
            return -1;
        }
    }

    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map toMap() {
        try {
            return jedisClient.hgetAll(name);
        } catch (Exception e) {
            log.warn("fail to build CacheEntry. return EmptyMap.", e);
            return Collections.emptyMap();
        }
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public int getTimeout() {
        return cacheLockTimeout;
    }
}
