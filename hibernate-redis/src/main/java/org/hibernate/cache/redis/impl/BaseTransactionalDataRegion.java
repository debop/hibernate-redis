package org.hibernate.cache.redis.impl;

import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TransactionalDataRegion;

/**
 * org.hibernate.cache.redis.impl.BaseTransactionalDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오전 11:33
 */
public abstract class BaseTransactionalDataRegion extends BaseRegion implements TransactionalDataRegion {

    private final CacheDataDescription metadata;

    protected BaseTransactionalDataRegion(RedisClient redis, String name, CacheDataDescription metadata, RegionFactory factory) {
        super(redis, name, factory);
        this.metadata = metadata;
    }

    @Override
    public CacheDataDescription getCacheDataDescription() {
        return metadata;
    }
}
