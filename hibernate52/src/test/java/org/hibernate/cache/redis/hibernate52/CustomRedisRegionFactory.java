package org.hibernate.cache.redis.hibernate52;

import lombok.NonNull;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.client.RedisClientFactory;
import org.hibernate.cache.redis.util.CacheTimestamper;
import org.hibernate.cache.redis.util.RedisCacheUtil;

import java.util.Properties;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class CustomRedisRegionFactory extends SingletonRedisRegionFactory {

    public CustomRedisRegionFactory(@NonNull Properties props) {
        super(props);
    }

    @Override
    public RedisClient createRedisClient() {
        return RedisClientFactory.createRedisClient(RedisCacheUtil.getRedissonConfigPath());
    }

    @Override
    public CacheTimestamper createCacheTimestamper(RedisClient redisClient, String cacheKey) {
        // Generates increasing identifiers (in a single VM only).
        return new CacheTimestamper() {
            @Override
            public long next() {
                return System.currentTimeMillis();
            }
        };
    }
}
