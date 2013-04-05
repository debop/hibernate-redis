package org.hibernate.cache.redis.regions;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * An naturalId region specific wrapper around an Redis.
 * <p/>
 * This implementation returns Redis specific access strategy instances for all the non-transactional access types.
 * Transactional access is not supported.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:55
 */
public class RedisNaturalIdRegion extends RedisTransactionalDataRegion implements NaturalIdRegion {

    public RedisNaturalIdRegion(IRedisAccessStrategyFactory accessStrategyFactory,
                                RedisClient redis,
                                String regionName,
                                Settings settings,
                                CacheDataDescription metadata,
                                Properties props) {
        super(accessStrategyFactory, redis, regionName, settings, metadata, props);
    }

    @Override
    public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return accessStrategyFactory.createNaturalIdRegionAccessStrategy(this, accessType);
    }
}
