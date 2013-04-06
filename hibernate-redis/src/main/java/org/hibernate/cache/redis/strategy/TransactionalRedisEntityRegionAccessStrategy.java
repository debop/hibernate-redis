package org.hibernate.cache.redis.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.regions.RedisEntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * TransactionalRedisEntityRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalRedisEntityRegionAccessStrategy
        extends AbstractRedisAccessStrategy<RedisEntityRegion>
        implements EntityRegionAccessStrategy {

    private final RedisClient redis;

    public TransactionalRedisEntityRegionAccessStrategy(RedisEntityRegion region, RedisClient redis, Settings settings) {
        super(region, settings);
        this.redis = redis;
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        try {
            return redis.get(region.getRegionedKey(key));
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        try {
            String regionedKey = region.getRegionedKey(key);
            if (minimalPutOverride && redis.exists(regionedKey))
                return false;

            redis.set(regionedKey, value);
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
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        try {
            redis.set(region.getRegionedKey(key), value);
            return true;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
        try {
            redis.set(region.getRegionedKey(key), value);
            return true;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
            throws CacheException {
        return false;
    }

    @Override
    public void remove(Object key) throws CacheException {
        try {
            redis.delete(region.getRegionedKey(key));
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
}
