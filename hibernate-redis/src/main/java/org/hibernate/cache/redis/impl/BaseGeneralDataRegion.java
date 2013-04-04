package org.hibernate.cache.redis.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.cache.spi.RegionFactory;
import redis.clients.jedis.Jedis;

/**
 * org.hibernate.cache.redis.impl.BaseGeneralDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오전 11:22
 */
@Slf4j
public abstract class BaseGeneralDataRegion extends BaseRegion implements GeneralDataRegion {

    @Getter
    private final Jedis jedis;

    protected BaseGeneralDataRegion(Jedis jedis, String name, RegionFactory factory) {
        super(jedis, name, factory);
        this.jedis = jedis;
    }

    @Override
    public void evict(Object key) throws CacheException {
        jedis.del((String) key);
    }

    @Override
    public void evictAll() throws CacheException {
        invalidateRegion();
    }

    @Override
    public Object get(Object key) throws CacheException {
        return jedis.get((String) key);
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        jedis.setex((String) key, getTimeout(), (String) value);
    }
}
