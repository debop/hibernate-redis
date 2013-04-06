package org.hibernate.cache.redis.regions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * RedisTransactionalDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 9:44
 */
@Slf4j
public class RedisTransactionalDataRegion extends RedisDataRegion implements TransactionalDataRegion {

    /**
     * Hibernate settings associated with the persistence unit.
     */
    protected final Settings settings;
    /**
     * Metadata associated with the objects sorted in the region
     */
    protected final CacheDataDescription metadata;

    public RedisTransactionalDataRegion(IRedisAccessStrategyFactory accessStrategyFactory,
                                        RedisClient redis,
                                        String regionName,
                                        Settings settings,
                                        CacheDataDescription metadata,
                                        Properties props) {
        super(accessStrategyFactory, redis, regionName, props);

        this.settings = settings;
        this.metadata = metadata;
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public boolean isTransactionAware() {
        return false;
    }

    @Override
    public CacheDataDescription getCacheDataDescription() {
        return metadata;
    }

    public Object get(Object key) throws CacheException {
        String regionedKey = getRegionedKey(key);
        if (log.isTraceEnabled())
            log.trace("Get key=[{}]", regionedKey);

        return redis.get(regionedKey);
    }


    public void put(Object key, Object value) throws CacheException {
        String regionedKey = getRegionedKey(key);

        if (log.isTraceEnabled())
            log.trace("Put key=[{}], value=[{}]", regionedKey, value);

        redis.set(regionedKey, value);
    }

    public void remove(Object key) throws CacheException {
        String regionedKey = getRegionedKey(key);

        if (log.isTraceEnabled())
            log.trace("Remove key=[{}]", key);

        redis.delete(regionedKey);
    }


    public void clear() throws CacheException {
        if (log.isTraceEnabled())
            log.trace("Clear");

        redis.deleteRegion(getRegionPrefix());
    }

    public void writeLock(Object key) { }

    public void writeUnlock(Object key) { }

    public void readLock(Object key) { }

    public void readUnlock(Object key) { }

    public void evict(Object key) throws CacheException {
        String regionedKey = getRegionedKey(key);

        if (log.isTraceEnabled())
            log.trace("Evict key=[{}]", regionedKey);

        redis.delete(regionedKey);
    }

    public void evictAll() throws CacheException {

        if (log.isTraceEnabled())
            log.trace("EvictAll");

        redis.deleteRegion(getRegionPrefix());
    }

    /**
     * Returns <code>true</code> if the locks used by the locking methods of this region are the independent of the cache.
     * <p/>
     * Independent locks are not locked by the cache when the cache is accessed directly.  This means that for an independent lock
     * lock holds taken through a region method will not block direct access to the cache via other means.
     */
    public final boolean locksAreIndependentOfCache() {
        return false;
    }
}
