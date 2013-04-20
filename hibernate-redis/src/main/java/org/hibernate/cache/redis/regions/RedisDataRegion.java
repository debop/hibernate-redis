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
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.redis.util.Timestamper;
import org.hibernate.cache.spi.Region;

import java.util.*;

/**
 * RedisDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:48
 */
@Slf4j
public abstract class RedisDataRegion implements Region {

    private static final String CACHE_LOCK_TIMEOUT_PROPERTY = "io.redis.hibernate.cache_lock_timeout";
    private static final int DEFAULT_CACHE_LOCK_TIMEOUT = 60 * 1000; // 60 seconds

    public static final String REGION_SEPARATOR = ":-:";

    protected final IRedisAccessStrategyFactory accessStrategyFactory;
    /**
     * Region name
     */
    private final String name;
    /**
     * Redis client instance deal hibernate data region.
     */
    @Getter
    protected final RedisClient redis;

    @Getter
    private final int cacheLockTimeout; // milliseconds

    protected RedisDataRegion(IRedisAccessStrategyFactory accessStrategyFactory,
                              RedisClient redis,
                              String regionName,
                              Properties props) {
        this.accessStrategyFactory = accessStrategyFactory;
        this.redis = redis;
        this.name = regionName;

        this.cacheLockTimeout = Integer.decode(props.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY,
                                                                 Integer.toString(DEFAULT_CACHE_LOCK_TIMEOUT)));
    }

    public String getName() {
        return name;
    }

    @Override
    public void destroy() throws CacheException {
        try {
            redis.deleteRegion(getName());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean contains(Object key) {
        return redis.exists(key);
    }

    @Override
    public long getSizeInMemory() {
        return redis.dbSize();
    }

    @Override
    public long getElementCountInMemory() {
        if (log.isTraceEnabled())
            log.trace("getElementCountInMemory... region=[{}]", name);
        return redis.keysInRegion(name).size();
    }

    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map toMap() {
        try {
            Map result = new HashMap();
            Set keys = redis.keysInRegion(name);
            List<Object> values = redis.mget(keys);

            int i = 0;
            for (Object key : keys) {
                result.put(key, values.get(i++));
            }
            return result;
        } catch (Exception e) {
            log.error("CacheEntry를 만드는데 실패했습니다.", e);
            throw new CacheException(e);
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
