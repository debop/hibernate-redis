package org.hibernate.cache.redis.impl;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.cache.spi.RegionFactory;

import java.util.concurrent.TimeUnit;

/**
 * org.hibernate.cache.redis.impl.BaseGeneralDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오전 11:22
 */
@Slf4j
public abstract class BaseGeneralDataRegion extends BaseRegion implements GeneralDataRegion {

    protected BaseGeneralDataRegion(RedisClient redis, String name, RegionFactory factory) {
        super(redis, name, factory);
    }

    @Override
    public void evict(Object key) throws CacheException {
        getRedis().delete((String) key);
    }

    @Override
    public void evictAll() throws CacheException {
        invalidateRegion();
    }

    @Override
    public Object get(Object key) throws CacheException {
        return getRedis().opsForValue().get(key);
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        getRedis().opsForValue().set((String) key, value, getTimeout(), TimeUnit.SECONDS);
    }
}
