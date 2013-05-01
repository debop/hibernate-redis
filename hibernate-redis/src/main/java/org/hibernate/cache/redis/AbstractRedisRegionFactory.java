/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.regions.*;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.redis.strategy.RedisAccessStrategyFactoryImpl;
import org.hibernate.cache.redis.util.JedisTool;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Abstract implementation of an Redis specific RegionFactory.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:59
 */
abstract class AbstractRedisRegionFactory implements RegionFactory {

    private static final Logger log = LoggerFactory.getLogger(AbstractRedisRegionFactory.class);
    private static final boolean isTraceEnabled = log.isTraceEnabled();
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    /**
     * The Hibernate system property specifying the location of the redis configuration file name.
     * <p/>
     * If not set, redis.xml will be looked for in the root of the classpath.
     * <p/>
     * If set to say redis-1.xml, redis-1.xml will be looked for in the root of the classpath.
     */
    public static final String IO_REDIS_CACHE_CONFIGURATION_RESOURCE_NAME = "io.redis.cache.configurationResourceName";

    /** Settings object for the Hibernate persistence unit. */
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
        if (isDebugEnabled)
            log.debug("EntityRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisEntityRegion(accessStrategyFactory,
                                     JedisTool.createJedisClient(regionName, properties),
                                     regionName,
                                     settings,
                                     metadata,
                                     properties);
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName,
                                                Properties properties,
                                                CacheDataDescription metadata) throws CacheException {
        if (isDebugEnabled)
            log.debug("NaturalIdRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisNaturalIdRegion(accessStrategyFactory,
                                        JedisTool.createJedisClient(regionName, properties),
                                        regionName,
                                        settings,
                                        metadata,
                                        properties);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName,
                                                  Properties properties,
                                                  CacheDataDescription metadata) throws CacheException {
        if (isDebugEnabled)
            log.debug("CollectionRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisCollectionRegion(accessStrategyFactory,
                                         JedisTool.createJedisClient(regionName, properties),
                                         regionName,
                                         settings,
                                         metadata,
                                         properties);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        if (isDebugEnabled)
            log.debug("QueryResultsRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisQueryResultsRegion(accessStrategyFactory,
                                           JedisTool.createJedisClient(regionName, properties),
                                           regionName,
                                           properties);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        if (isDebugEnabled)
            log.debug("TimestampsRegion을 빌드합니다. Region=[{}]", regionName);

        return new RedisTimestampsRegion(accessStrategyFactory,
                                         JedisTool.createJedisClient(regionName, properties),
                                         regionName,
                                         properties);
    }
}
