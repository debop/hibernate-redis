package org.hibernate.cache.redis.client;

import org.hibernate.cache.redis.util.CacheTimestamper;

import java.util.Collections;
import java.util.List;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class RedisTimestamper implements CacheTimestamper {

    private final RedisClient redisClient;
    private final List<Object> timestampCacheKey;

    public RedisTimestamper(RedisClient redisClient, String cacheKey) {
        this.redisClient = redisClient;
        this.timestampCacheKey = Collections.<Object>singletonList(cacheKey + "timestamp");
    }

    @Override
    public long next() {
        return redisClient.nextTimestamp(timestampCacheKey);
    }
}
