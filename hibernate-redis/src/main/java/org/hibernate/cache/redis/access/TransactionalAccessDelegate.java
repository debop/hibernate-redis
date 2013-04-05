package org.hibernate.cache.redis.access;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.impl.BaseRegion;

import java.util.concurrent.TimeUnit;

/**
 * org.hibernate.cache.redis.access.TransactionalAccessDelegate
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 1:22
 */
@Slf4j
public class TransactionalAccessDelegate {

    private final RedisClient redis;
    private final BaseRegion region;
    private final PutFromLoadValidator putValidator;

    public TransactionalAccessDelegate(BaseRegion region, PutFromLoadValidator validator) {
        this.region = region;
        this.redis = region.getRedis();
        this.putValidator = validator;
    }

    public Object get(String key, long txTimestamp) throws CacheException {
        if (!region.checkValid())
            return null;

        Object val = redis.get(key);
        if (val == null)
            putValidator.registerPendingPut(key);
        return val;
    }

    public boolean putFromLoad(String key, Object value, long txTimestamp, Object version) {
        return putFromLoad(key, value, txTimestamp, version, false);
    }

    public boolean putFromLoad(String key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException {
        if (!region.checkValid()) {
            if (log.isTraceEnabled())
                log.trace("Region [{}] not valid", region.getName());
            return false;
        }

        if (minimalPutOverride && redis.exists(key))
            return false;

        if (!putValidator.acquirePutFromLoadLock(key)) {
            if (log.isTraceEnabled())
                log.trace("Put from load lock not acquired for key [{}]", key);
            return false;
        }

        try {
            redis.opsForValue().set(key, value, region.getTimeout(), TimeUnit.SECONDS);
        } finally {
            putValidator.releasePutFromLoadLock(key);
        }
        return true;
    }

}
