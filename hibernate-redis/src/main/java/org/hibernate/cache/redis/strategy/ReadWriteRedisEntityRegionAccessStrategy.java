package org.hibernate.cache.redis.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.regions.RedisEntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * ReadWriteRedisEntityRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:13
 */
public class ReadWriteRedisEntityRegionAccessStrategy
        extends AbstractReadWriteRedisAccessStrategy<RedisEntityRegion>
        implements EntityRegionAccessStrategy {

    /**
     * Creates a read/write cache access strategy around the given cache region.
     */
    public ReadWriteRedisEntityRegionAccessStrategy(RedisEntityRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        region.writeLock(key);
        try {
            Lockable item = (Lockable) region.get(key);
            if (item == null) {
                region.put(key, new Item(value, version, region.nextTimestamp()));
                return true;
            }
            return false;
        } finally {
            region.writeUnlock(key);
        }
    }

    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
        return false;
    }

    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
        region.writeLock(key);
        try {
            Lockable item = (Lockable) region.get(key);

            if (item != null && item.isUnlockable(lock)) {
                Lock lockItem = (Lock) item;
                if (lockItem.wasLockedConcurrently()) {
                    decrementLock(key, lockItem);
                    return false;
                } else {
                    region.put(key, new Item(value, currentVersion, region.nextTimestamp()));
                    return true;
                }
            } else {
                handleLockExpiry(key, item);
                return false;
            }
        } finally {
            region.writeUnlock(key);
        }
    }
}
