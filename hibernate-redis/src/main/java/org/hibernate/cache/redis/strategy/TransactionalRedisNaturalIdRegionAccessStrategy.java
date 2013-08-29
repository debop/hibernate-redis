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

package org.hibernate.cache.redis.strategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cache.redis.regions.RedisNaturalIdRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * TransactionalRedisNaturalIdRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalRedisNaturalIdRegionAccessStrategy
    extends AbstractRedisAccessStrategy<RedisNaturalIdRegion>
    implements NaturalIdRegionAccessStrategy {

    @Getter
    private final JedisClient jedisClient;

    public TransactionalRedisNaturalIdRegionAccessStrategy(RedisNaturalIdRegion region,
                                                           JedisClient jedis,
                                                           Settings settings) {
        super(region, settings);
        this.jedisClient = jedis;
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        return false;
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        try {
            return jedisClient.get(key);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public NaturalIdRegion getRegion() {
        return region();
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        log.trace("캐시를 추가합니다... key=[{}], value=[{}]", key, value);
        try {
            jedisClient.set(key, value);
            return true;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public boolean putFromLoad(Object key,
                               Object value,
                               long txTimestamp,
                               Object version,
                               boolean minimalPutOverride) throws CacheException {
        log.trace("엔티티를 로드하고, 캐시에 저장합니다... key=[{}]", key);

        try {
            if (minimalPutOverride && jedisClient.exists(key))
                return false;
            jedisClient.set(key, value);
            return true;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }


    @Override
    public void remove(Object key) throws CacheException {
        log.trace("캐시를 삭제합니다. key=[{}]", key);

        try {
            jedisClient.delete(key);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        // nothing to do
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        log.trace("캐시를 갱신합니다... key=[{}], value=[{}]", key, value);
        try {
            jedisClient.set(key, value);
            return true;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
}
