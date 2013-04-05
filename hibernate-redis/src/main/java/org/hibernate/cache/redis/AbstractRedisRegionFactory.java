package org.hibernate.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.regions.*;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactoryImpl;
import org.hibernate.cache.redis.util.RedisTool;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * Abstract implementation of an Redis specific RegionFactory.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:59
 */
@Slf4j
abstract class AbstractRedisRegionFactory implements RegionFactory {

    /**
     * The Hibernate system property specifying the location of the redis configuration file name.
     * <p/>
     * If not set, redis.xml will be looked for in the root of the classpath.
     * <p/>
     * If set to say redis-1.xml, redis-1.xml will be looked for in the root of the classpath.
     */
    public static final String IO_REDIS_CACHE_CONFIGURATION_RESOURCE_NAME = "io.redis.cache.configurationResourceName";

    /**
     * Settings object for the Hibernate persistence unit.
     */
    protected Settings settings;

    protected final Properties props;

    protected final IRedisAccessStrategyFactory accessStrategyFactory = new RedisAccessStrategyFactoryImpl();

    public AbstractRedisRegionFactory(Properties props) {
        this.props = props;
    }

    /**
     * Whether to optimize for minimals puts or minimal gets.
     * <p/>
     * Indicates whether when operating in non-strict read/write or read-only mode
     * Hibernate should optimize the access patterns for minimal puts or minimal gets.
     * In Ehcache we default to minimal puts since this should have minimal to no
     * affect on unclustered users, and has great benefit for clustered users.
     * <p/>
     * This setting can be overridden by setting the "hibernate.cache.use_minimal_puts"
     * property in the Hibernate configuration.
     *
     * @return true, optimize for minimal puts
     */
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName,
                                          Properties properties,
                                          CacheDataDescription metadata) throws CacheException {
        if (log.isDebugEnabled())
            log.debug("EntityRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisEntityRegion(accessStrategyFactory,
                                     RedisTool.createRedisClient(properties),
                                     regionName,
                                     settings,
                                     metadata,
                                     properties);
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName,
                                                Properties properties,
                                                CacheDataDescription metadata) throws CacheException {
        if (log.isDebugEnabled())
            log.debug("NaturalIdRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisNaturalIdRegion(accessStrategyFactory,
                                        RedisTool.createRedisClient(properties),
                                        regionName,
                                        settings,
                                        metadata,
                                        properties);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName,
                                                  Properties properties,
                                                  CacheDataDescription metadata) throws CacheException {
        if (log.isDebugEnabled())
            log.debug("CollectionRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisCollectionRegion(accessStrategyFactory,
                                         RedisTool.createRedisClient(properties),
                                         regionName,
                                         settings,
                                         metadata,
                                         properties);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        if (log.isDebugEnabled())
            log.debug("QueryResultsRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisQueryResultsRegion(accessStrategyFactory,
                                           RedisTool.createRedisClient(properties),
                                           regionName,
                                           properties);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        if (log.isDebugEnabled())
            log.debug("TimestampsRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisTimestampsRegion(accessStrategyFactory,
                                         RedisTool.createRedisClient(properties),
                                         regionName,
                                         properties);
    }
}
