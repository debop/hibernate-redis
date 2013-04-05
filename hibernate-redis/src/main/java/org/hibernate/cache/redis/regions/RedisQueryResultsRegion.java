package org.hibernate.cache.redis.regions;

import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.spi.QueryResultsRegion;

import java.util.Properties;

/**
 * A query results region specific wrapper around an Redis instance.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:55
 */
public class RedisQueryResultsRegion extends RedisGeneralDataRegion implements QueryResultsRegion {

    public RedisQueryResultsRegion(IRedisAccessStrategyFactory accessStrategyFactory,
                                   RedisClient redis,
                                   String regionName,
                                   Properties props) {
        super(accessStrategyFactory, redis, regionName, props);
    }
}
