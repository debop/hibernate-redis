package org.hibernate.cache.redis.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.regions.RedisCollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * Redis specific non-strict read/write collection region access strategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 10:42
 */
public class NonStrictReadWriteRedisCollectionRegionAccessStrategy
        extends AbstractRedisAccessStrategy<RedisCollectionRegion>
        implements CollectionRegionAccessStrategy {


    public NonStrictReadWriteRedisCollectionRegionAccessStrategy(RedisCollectionRegion region, Settings settings) {
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
}
