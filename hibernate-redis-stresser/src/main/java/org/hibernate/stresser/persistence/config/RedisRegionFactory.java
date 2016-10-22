package org.hibernate.stresser.persistence.config;

import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.hibernate5.SingletonRedisRegionFactory;
import org.hibernate.cache.redis.util.CacheTimestamper;
import org.hibernate.cache.redis.util.Timestamper;

import java.util.Properties;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class RedisRegionFactory extends SingletonRedisRegionFactory {

    public RedisRegionFactory(Properties props) {
        super(props);
    }

    @Override
    public CacheTimestamper createCacheTimestamper(RedisClient redisClient, String cacheKey) {
        return new Timestamper();
    }

    @Override
    public RedisClient createRedisClient() {
        return new RedisClient(ApplicationContextProvider.getAutowireCapableBeanFactory().getBean(RedisClientProvider.class).getRedisClient());
    }
}
