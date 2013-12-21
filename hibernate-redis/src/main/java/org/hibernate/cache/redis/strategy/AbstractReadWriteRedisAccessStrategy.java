/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.regions.RedisTransactionalDataRegion;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Superclass for all Redis specific read/write AccessStrategy implementations.
 *
 * @param <T> the type of the enclosed cache region
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 10:07
 */
@Slf4j
public class AbstractReadWriteRedisAccessStrategy<T extends RedisTransactionalDataRegion>
        extends AbstractRedisAccessStrategy<T> {

    private final UUID uuid = UUID.randomUUID();
    private final AtomicLong nextLockId = new AtomicLong();
    private final Comparator versionComparator;

    /**
     * Creates a read/write cache access strategy around the given cache region.
     */
    public AbstractReadWriteRedisAccessStrategy(T region, Settings settings) {
        super(region, settings);
        this.versionComparator = region.getCacheDataDescription().getVersionComparator();
    }

    /**
     * Returns <code>null</code> if the item is not readable.  Locked items are not readable, nor are items created
     * after the start of this transaction.
     */
    public final Object get(Object key, long txTimestamp) {
        log.debug("get cache item... key=[{}], txTimestamp=[{}]", key, txTimestamp);

        readLockIfNeeded(key);
        try {
            return region.get(key);
        } catch (Exception e) {
            log.warn("Fail to retrieve redis cache item", e);
            return null;
        } finally {
            readUnlockIfNeeded(key);
        }
    }

    @Override
    public final boolean putFromLoad(Object key,
                                     Object value,
                                     long txTimestamp,
                                     Object version,
                                     boolean minimalPutOverride) {
        log.debug("set cache item after entity loading... key=[{}], value=[{}], txTimestamp=[{}], version=[{}], minimalPutOverride=[{}]",
                  key, value, txTimestamp, version, minimalPutOverride);

        region.writeLock(key);
        try {
            Object item = region.get(key);
            region.put(key, value);
            return true;
        } catch (Exception e) {
            log.warn("Fail to put cache item", e);
            return false;
        } finally {
            region.writeUnlock(key);
        }
    }

    /**
     * Soft-lock a cache item.
     */
    public final SoftLock lockItem(Object key, Object version) {
        log.debug("lock cache item... key=[{}], version=[{}]", key, version);
        // region.remove(key);
        return null;
    }

    /**
     * Soft-unlock a cache item.
     */
    public final void unlockItem(Object key, SoftLock lock) {
        log.debug("unlock cache item... key=[{}], lock=[{}]", key, lock);
        // region.remove(key);
    }


    private long nextLockId() {
        return nextLockId.getAndIncrement();
    }

    /**
     * Read lock the entry for the given key if internal cache locks will not provide correct exclusion.
     */
    private void readLockIfNeeded(Object key) {
        if (region.locksAreIndependentOfCache()) {
            region.readLock(key);
        }
    }

    /**
     * Read unlock the entry for the given key if internal cache locks will not provide correct exclusion.
     */
    private void readUnlockIfNeeded(Object key) {
        if (region.locksAreIndependentOfCache()) {
            region.readUnlock(key);
        }
    }
}
