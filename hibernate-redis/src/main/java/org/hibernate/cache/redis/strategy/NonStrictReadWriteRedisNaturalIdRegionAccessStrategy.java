package org.hibernate.cache.redis.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.regions.RedisNaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * Redis specific non-strict read/write NaturalId region access strategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:06
 */
public class NonStrictReadWriteRedisNaturalIdRegionAccessStrategy
        extends AbstractRedisAccessStrategy<RedisNaturalIdRegion>
        implements NaturalIdRegionAccessStrategy {

    /**
     * Create a non-strict read/write access strategy accessing the given NaturalId region.
     */
    public NonStrictReadWriteRedisNaturalIdRegionAccessStrategy(RedisNaturalIdRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        return region.get(key);
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && region.contains(key))
            return false;

        region.put(key, value);
        return true;
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        region.remove(key);
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        remove(key);
        return false;
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        unlockItem(key, lock);
        return false;
    }

    @Override
    public void remove(Object key) throws CacheException {
        region.remove(key);
    }
}
