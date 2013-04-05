package org.hibernate.cache.redis.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.regions.RedisNaturalIdRegion;
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

    private final RedisClient redis;

    public TransactionalRedisNaturalIdRegionAccessStrategy(RedisNaturalIdRegion region, RedisClient redis, Settings settings) {
        super(region, settings);
        this.redis = redis;
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        try {
            return redis.get(key.toString());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        try {
            if (minimalPutOverride && redis.exists(key.toString()))
                return false;

            redis.set(key, value);
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
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        // nothing to do
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        try {
            redis.set(key.toString(), value);
            return true;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        try {
            redis.set(key, value);
            return true;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        return false;
    }

    @Override
    public void remove(Object key) throws CacheException {
        try {
            redis.delete(key.toString());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

}
