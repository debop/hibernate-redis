package org.hibernate.cache.redis.regions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.redis.util.Timestamper;
import org.hibernate.cache.spi.Region;

import java.util.*;

/**
 * RedisDataRegion
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:48
 */
@Slf4j
public abstract class RedisDataRegion implements Region {

    private static final String CACHE_LOCK_TIMEOUT_PROPERTY = "io.redis.hibernate.cache_lock_timeout";
    private static final int DEFAULT_CACHE_LOCK_TIMEOUT = 60 * 1000; // 60 seconds

    private static final String CACHE_TIMEOUT_PROPERTY = "io.redis.hibernate.cache_timeout";
    private static final int DEFAULT_CACHE_TIMEOUT = 5 * 60 * 1000; // 5 minutes

    protected final IRedisAccessStrategyFactory accessStrategyFactory;
    /**
     * Region name
     */
    @Getter
    private final String name;
    /**
     * Redis client instance deal hibernate data region.
     */
    @Getter
    protected final RedisClient redis;

    @Getter
    private final int cacheLockTimeout; // milliseconds
    @Getter
    private final int cacheTimeout;

    protected RedisDataRegion(IRedisAccessStrategyFactory accessStrategyFactory,
                              RedisClient redis,
                              String regionName,
                              Properties props) {
        this.accessStrategyFactory = accessStrategyFactory;
        this.redis = redis;
        this.name = regionName;

        this.cacheLockTimeout = Integer.decode(props.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY,
                                                                 Integer.toString(DEFAULT_CACHE_LOCK_TIMEOUT)));
        this.cacheTimeout = Integer.decode(props.getProperty(CACHE_TIMEOUT_PROPERTY,
                                                             Integer.toString(DEFAULT_CACHE_TIMEOUT)));
    }

    @Override
    public void destroy() throws CacheException {
        if (RedisDataRegion.log.isDebugEnabled())
            RedisDataRegion.log.debug("Region 을 삭제합니다... Region=[{}]", name);

        try {
            redis.deleteRegion(getName());
        } catch (Exception e) {
            throw new CacheException(e);
        }
        if (RedisDataRegion.log.isDebugEnabled())
            RedisDataRegion.log.debug("Region 을 삭제했습니다. Region=[{}]", name);
    }

    @Override
    public boolean contains(Object key) {
        return redis.exists(key.toString());
    }

    @Override
    public long getSizeInMemory() {
        return redis.dbSize();
    }

    @Override
    public long getElementCountInMemory() {
        return redis.keys(name + ":*").size();
    }

    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    @Override
    public Map toMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        Set<String> keys = redis.keys(name + ":*");
        List<Object> values = redis.mget(keys);

        int i = 0;
        for (String key : keys) {
            result.put(key, values.get(i++));
        }
        return result;
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public int getTimeout() {
        return cacheLockTimeout;
    }
}
