package org.hibernate.cache.redis.regions;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * An entity region specific wrapper around an Redis.
 * <p/>
 * This implementation returns Redis specific access strategy instances for all the non-transactional access types.
 * Transactional access is not supported.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:51
 */
@Slf4j
public class RedisEntityRegion extends RedisTransactionalDataRegion implements EntityRegion {

    public RedisEntityRegion(IRedisAccessStrategyFactory accessStrategyFactory,
                             RedisClient redis,
                             String regionName,
                             Settings settings,
                             CacheDataDescription metadata, Properties props) {
        super(accessStrategyFactory, redis, regionName, settings, metadata, props);
    }

    @Override
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return accessStrategyFactory.createEntityRegionAccessStrategy(this, accessType);
    }
}
