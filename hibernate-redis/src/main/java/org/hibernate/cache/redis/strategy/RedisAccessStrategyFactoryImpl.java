package org.hibernate.cache.redis.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.regions.RedisCollectionRegion;
import org.hibernate.cache.redis.regions.RedisEntityRegion;
import org.hibernate.cache.redis.regions.RedisNaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * org.hibernate.cache.redis.strategy.RedisAccessStrategyFactoryImpl
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:03
 */
@Slf4j
public class RedisAccessStrategyFactoryImpl implements IRedisAccessStrategyFactory {

    @Override
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion entityRegion, AccessType accessType) {

        if (log.isDebugEnabled())
            log.debug("create EntityRegionAccessStrategy. regionName=[{}], accessType=[{}]",
                      entityRegion.getName(), accessType.getExternalName());

        switch (accessType) {
            case READ_ONLY:
                if (entityRegion.getCacheDataDescription().isMutable()) {
                    log.warn("read-only cache configured for mutable entity [{}]", entityRegion.getName());
                }
                return new ReadOnlyRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteRedisEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalRedisEntityRegionAccessStrategy(entityRegion,
                                                                        entityRegion.getRedis(),
                                                                        entityRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    @Override
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(RedisCollectionRegion collectionRegion,
                                                                               AccessType accessType) {

        if (log.isDebugEnabled())
            log.debug("create CollectionRegionAccessStrategy. regionName=[{}], accessType=[{}]",
                      collectionRegion.getName(), accessType.getExternalName());

        switch (accessType) {
            case READ_ONLY:
                if (collectionRegion.getCacheDataDescription().isMutable()) {
                    log.warn("read-only cache configured for mutable entity [{}]", collectionRegion.getName());
                }
                return new ReadOnlyRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalRedisCollectionRegionAccessStrategy(collectionRegion,
                                                                            collectionRegion.getRedis(),
                                                                            collectionRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    @Override
    public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(RedisNaturalIdRegion naturalIdRegion,
                                                                             AccessType accessType) {

        if (log.isDebugEnabled())
            log.debug("create CollectionRegionAccessStrategy. regionName=[{}], accessType=[{}]",
                      naturalIdRegion.getName(), accessType.getExternalName());

        switch (accessType) {
            case READ_ONLY:
                if (naturalIdRegion.getCacheDataDescription().isMutable()) {
                    log.warn("read-only cache configured for mutable entity [{}]", naturalIdRegion.getName());
                }
                return new ReadOnlyRedisNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteRedisNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteRedisNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalRedisNaturalIdRegionAccessStrategy(naturalIdRegion,
                                                                           naturalIdRegion.getRedis(),
                                                                           naturalIdRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }
}
