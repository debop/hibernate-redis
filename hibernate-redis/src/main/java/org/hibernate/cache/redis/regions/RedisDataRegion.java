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
import org.hibernate.cache.redis.util.Timestamper;
import org.hibernate.cache.spi.Region;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Redis를 저장소로 사용하는 Data Region 의 기본 클래스입니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:48
 */
@Slf4j
public abstract class RedisDataRegion implements Region {

    private static final String CACHE_LOCK_TIMEOUT_PROPERTY = "io.redis.hibernate.cache_lock_timeout";
    private static final int DEFAULT_CACHE_LOCK_TIMEOUT = 60 * 1000; // 60 seconds

    private static final String EXPIRE_IN_SECONDS = "redis.expireInSeconds";

    @Getter
    protected final RedisAccessStrategyFactory accessStrategyFactory;

    /**
     * 영역명
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
        log.trace("RedisDataRegion ctor. region name=[{}]", regionName);
        this.accessStrategyFactory = accessStrategyFactory;
        this.jedisClient = jedisClient;
        this.name = regionName;

        this.cacheLockTimeout =
                Integer.decode(props.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY,
                                                 String.valueOf(DEFAULT_CACHE_LOCK_TIMEOUT)));

        this.expireInSeconds = Integer.decode(props.getProperty(EXPIRE_IN_SECONDS, "120"));
    }

    /**
     * 영역 명
     *
     * @return 영역 명
     */
    public String getName() {
        return name;
    }

    /**
     * 캐시 영역을 삭제합니다.
     *
     * @throws org.hibernate.cache.CacheException
     *
     */
    @Override
    public void destroy() throws CacheException {
        try {
            log.debug("영역[{}]을 삭제합니다.", getName());
            if (!regionDeleted) {
                jedisClient.deleteRegion(name);
                regionDeleted = true;
            }
        } catch (Exception ignored) {
            log.info("동시에 여러 서버에서 요청 시 예외가 발생할 수 있습니다. 무시합니다.");
        }
    }

    /**
     * 지정한 키에 해당하는 캐시 항목이 존재하는지 파악합니다.
     *
     * @param key 캐시 키
     * @return 캐시 항목 존재 여부
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
            log.warn("예외가 발생했습니다.", t);
            return -1;
        }
    }

    @Override
    public long getElementCountInMemory() {
        try {
            return jedisClient.keysInRegion(name).size();
        } catch (Exception e) {
            log.warn("예외가 발생했습니다.", e);
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
            log.warn("CacheEntry를 만드는데 실패했습니다. EmptyMap을 반환합니다.", e);
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
