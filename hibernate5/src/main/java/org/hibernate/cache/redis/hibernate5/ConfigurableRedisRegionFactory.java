package org.hibernate.cache.redis.hibernate5;

import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.util.CacheTimestamper;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public interface ConfigurableRedisRegionFactory {
    CacheTimestamper createCacheTimestamper(RedisClient redisClient, String cacheKey);
}
