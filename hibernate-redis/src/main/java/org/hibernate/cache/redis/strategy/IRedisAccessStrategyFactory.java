package org.hibernate.cache.redis.strategy;

import org.hibernate.cache.redis.regions.RedisCollectionRegion;
import org.hibernate.cache.redis.regions.RedisEntityRegion;
import org.hibernate.cache.redis.regions.RedisNaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * IRedisAccessStrategyFactory
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 8:50
 */
public interface IRedisAccessStrategyFactory {

    /**
     * Create {@link EntityRegionAccessStrategy} for the input {@link org.hibernate.cache.redis.regions.RedisEntityRegion} and {@link AccessType}
     *
     * @param entityRegion
     * @param accessType
     * @return the created {@link EntityRegionAccessStrategy}
     */
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion entityRegion, AccessType accessType);

    /**
     * Create {@link CollectionRegionAccessStrategy} for the input {@link org.hibernate.cache.redis.regions.RedisCollectionRegion} and {@link AccessType}
     *
     * @param collectionRegion
     * @param accessType
     * @return the created {@link org.hibernate.cache.redis.regions.RedisCollectionRegion}
     */
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(RedisCollectionRegion collectionRegion,
                                                                               AccessType accessType);

    /**
     * Create {@link CollectionRegionAccessStrategy} for the input {@link org.hibernate.cache.redis.regions.RedisNaturalIdRegion} and {@link AccessType}
     *
     * @param naturalIdRegion
     * @param accessType
     * @return the created {@link org.hibernate.cache.redis.regions.RedisNaturalIdRegion}
     */
    public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(RedisNaturalIdRegion naturalIdRegion,
                                                                             AccessType accessType);

}
