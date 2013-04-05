package org.hibernate.cache.redis.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.regions.RedisNaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * ReadWriteRedisNaturalIdRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:13
 */
@Slf4j
public class ReadWriteRedisNaturalIdRegionAccessStrategy
        extends AbstractReadWriteRedisAccessStrategy<RedisNaturalIdRegion>
        implements NaturalIdRegionAccessStrategy {

    /**
     * Creates a read/write cache access strategy around the given cache region.
     */
    public ReadWriteRedisNaturalIdRegionAccessStrategy(RedisNaturalIdRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        region.writeLock(key);
        try {
            Lockable item = (Lockable) region.get(key);
            if (item == null) {
                region.put(key, new Item(value, null, region.nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            region.writeUnlock(key);
        }
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        region.writeLock(key);
        try {
            Lockable item = (Lockable) region.get(key);

            if (item != null && item.isUnlockable(lock)) {
                Lock lockItem = (Lock) item;
                if (lockItem.wasLockedConcurrently()) {
                    decrementLock(key, lockItem);
                    return false;
                } else {
                    region.put(key, new Item(value, null, region.nextTimestamp()));
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
