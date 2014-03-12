package org.hibernate.test.strategy;

import org.hibernate.cache.redis.regions.RedisTransactionalDataRegion;
import org.hibernate.cache.redis.strategy.AbstractReadWriteRedisAccessStrategy;
import org.hibernate.cfg.Settings;

/**
 * org.hibernate.test.strategy.ItemValueExtractor
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오후 1:12
 */
public class ItemValueExtractor extends AbstractReadWriteRedisAccessStrategy<RedisTransactionalDataRegion> {
    /**
     * Creates a read/write cache access strategy around the given cache region.
     */
    public ItemValueExtractor(RedisTransactionalDataRegion region, Settings settings) {
        super(region, settings);
    }

    public static <T> T getValue(final Object entry) {
        return (T) entry;
    }
}
