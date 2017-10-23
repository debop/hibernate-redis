/*
 * Copyright (c) 2017. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.cache.redis.hibernate4;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.client.RedisClientFactory;
import org.hibernate.cache.redis.hibernate4.regions.*;
import org.hibernate.cache.redis.hibernate4.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.redis.hibernate4.strategy.RedisAccessStrategyFactoryImpl;
import org.hibernate.cache.redis.util.CacheTimestamper;
import org.hibernate.cache.redis.util.RedisCacheUtil;
import org.hibernate.cache.redis.util.Timestamper;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import org.redisson.config.Config;

import java.util.Properties;

/**
 * Abstract Hibernate 4.x 2nd Redis Region Factory
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:59
 */
@Slf4j
abstract class AbstractRedisRegionFactory implements RegionFactory, ConfigurableRedisRegionFactory {

  /**
   * Settings object for the Hibernate persistence unit.
   */
  protected Settings settings;

  protected final Properties props;

  protected final RedisAccessStrategyFactory accessStrategyFactory = new RedisAccessStrategyFactoryImpl();

  /**
   * RedisClient instance.
   */
  protected RedisClient redis = null;
  protected CacheTimestamper cacheTimestamper = null;

  protected AbstractRedisRegionFactory(Properties props) {
    this.props = props;
  }

  @Override
  public CacheTimestamper createCacheTimestamper(RedisClient redisClient, String cacheKey) {
    return new Timestamper();
  }

  public RedisClient createRedisClient(Config config) {
    return RedisClientFactory.createRedisClient(config);
  }

  public RedisClient createRedisClient() {
    return RedisClientFactory.createRedisClient(RedisCacheUtil.getRedissonConfigPath());
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
  @Override
  public boolean isMinimalPutsEnabledByDefault() {
    return true;
  }

  @Override
  public AccessType getDefaultAccessType() {
    return AccessType.READ_WRITE;
  }

  public long nextTimestamp() {
    return cacheTimestamper.next();
  }

  @Override
  public EntityRegion buildEntityRegion(String regionName,
                                        Properties properties,
                                        CacheDataDescription metadata) throws CacheException {
    return new RedisEntityRegion(accessStrategyFactory,
                                 redis,
                                 this,
                                 regionName,
                                 settings,
                                 metadata,
                                 properties);
  }

  @Override
  public NaturalIdRegion buildNaturalIdRegion(String regionName,
                                              Properties properties,
                                              CacheDataDescription metadata) throws CacheException {
    return new RedisNaturalIdRegion(accessStrategyFactory,
                                    redis,
                                    this,
                                    regionName,
                                    settings,
                                    metadata,
                                    properties);
  }

  @Override
  public CollectionRegion buildCollectionRegion(String regionName,
                                                Properties properties,
                                                CacheDataDescription metadata) throws CacheException {
    return new RedisCollectionRegion(accessStrategyFactory,
                                     redis,
                                     this,
                                     regionName,
                                     settings,
                                     metadata,
                                     properties);
  }

  @Override
  public QueryResultsRegion buildQueryResultsRegion(String regionName,
                                                    Properties properties) throws CacheException {
    return new RedisQueryResultsRegion(accessStrategyFactory,
                                       redis,
                                       this,
                                       regionName,
                                       properties);
  }

  @Override
  public TimestampsRegion buildTimestampsRegion(String regionName,
                                                Properties properties) throws CacheException {
    return new RedisTimestampsRegion(accessStrategyFactory,
                                     redis,
                                     this,
                                     regionName,
                                     properties);
  }

  private static final long serialVersionUID = -5441842686229077097L;
}
