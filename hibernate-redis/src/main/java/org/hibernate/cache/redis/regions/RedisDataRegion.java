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

    public static final String REGION_SEPARATOR = ":-:";

    protected final IRedisAccessStrategyFactory accessStrategyFactory;
    /**
     * Region name
     */
    @Getter
    private final String name;

    private final String regionPrefix;
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
        this.regionPrefix = name + REGION_SEPARATOR;

        this.cacheLockTimeout = Integer.decode(props.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY,
                                                                 Integer.toString(DEFAULT_CACHE_LOCK_TIMEOUT)));
        this.cacheTimeout = Integer.decode(props.getProperty(CACHE_TIMEOUT_PROPERTY,
                                                             Integer.toString(DEFAULT_CACHE_TIMEOUT)));
    }

    public final String getRegionPrefix() {
        return regionPrefix;
    }

    public final String getRegionedKey(Object key) {
        return regionPrefix + ((key != null) ? key.toString() : "");
    }

    public final String getKeyWithoutRegion(String regionedKey) {
        int index = regionedKey.indexOf(regionPrefix);
        if (index < 0)
            return regionedKey;

        return regionedKey.substring(index + regionPrefix.length());
    }

    @Override
    public void destroy() throws CacheException {
        try {
            redis.deleteRegion(getRegionPrefix());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean contains(Object key) {
        return redis.exists(getRegionedKey(key));
    }

    @Override
    public long getSizeInMemory() {
        return redis.dbSize();
    }

    @Override
    public long getElementCountInMemory() {
        return redis.keys(getRegionedKey("*")).size();
    }

    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    @Override
    public Map toMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        Set<String> keys = redis.keys(getRegionedKey("*"));
        List<Object> values = redis.mget(keys);

        int i = 0;
        for (String key : keys) {
            result.put(getRegionedKey(key), values.get(i++));
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
