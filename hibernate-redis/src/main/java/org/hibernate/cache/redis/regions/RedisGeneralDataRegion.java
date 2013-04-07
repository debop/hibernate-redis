package org.hibernate.cache.redis.regions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.spi.GeneralDataRegion;

import java.util.Properties;

/**
 * RedisGeneralDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 9:00
 */
@Slf4j
public abstract class RedisGeneralDataRegion extends RedisDataRegion implements GeneralDataRegion {

    protected RedisGeneralDataRegion(IRedisAccessStrategyFactory accessStrategyFactory,
                                     RedisClient redis,
                                     String regionName,
                                     Properties props) {
        super(accessStrategyFactory, redis, regionName, props);
    }

    @Override
    public Object get(Object key) throws CacheException {
        if (log.isTraceEnabled())
            log.trace("Get key=[{}]", key);

        return redis.get(key);
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        if (log.isTraceEnabled())
            log.trace("Put key=[{}], value=[{}]", key, value);

        redis.set(key, value);
    }

    @Override
    public void evict(Object key) throws CacheException {
        if (log.isTraceEnabled())
            log.trace("Evict key=[{}]", key);

        redis.delete(key);
    }

    @Override
    public void evictAll() throws CacheException {
        if (log.isTraceEnabled())
            log.trace("EvictAll");

        redis.deleteRegion(getName());
    }
}
