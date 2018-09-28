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

package org.hibernate.cache.redis.hibernate52;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.client.RedisClient;
import org.hibernate.cache.redis.client.RedisClientFactory;
import org.hibernate.cache.redis.hibernate52.regions.*;
import org.hibernate.cache.redis.hibernate52.strategy.RedisAccessStrategyFactory;
import org.hibernate.cache.redis.hibernate52.strategy.RedisAccessStrategyFactoryImpl;
import org.hibernate.cache.redis.util.CacheTimestamper;
import org.hibernate.cache.redis.util.RedisCacheUtil;
import org.hibernate.cache.redis.util.Timestamper;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.internal.SimpleCacheKeysFactory;
import org.hibernate.cfg.Environment;
import org.redisson.config.Config;

import java.util.Properties;

/**
 * Abstract Hibernate 5.2.x or higher s2nd Redis Region Factory
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2015. 8. 27.
 */
@Slf4j
public abstract class AbstractRedisRegionFactory implements RegionFactory, ConfigurableRedisRegionFactory {

  protected final Properties props;
  protected SessionFactoryOptions options;
  protected final RedisAccessStrategyFactory accessStrategyFactory = new RedisAccessStrategyFactoryImpl();

  /**
   * {@link RedisClient} instance.
   */
  protected volatile RedisClient redis = null;
  protected CacheTimestamper cacheTimestamper = null;

  protected CacheKeysFactory cacheKeysFactory = null;

  protected AbstractRedisRegionFactory(@NonNull Properties props) {
    this.props = props;
    prepareCacheKeysFactory(props);
  }

  /**
   * Query properties for {@link Environment#CACHE_KEYS_FACTORY} definition in order to
   * support more than {@link DefaultCacheKeysFactory}. If no definition is found or there
   * is a problem with the implementation class {@link DefaultCacheKeysFactory#INSTANCE} is
   * set as default.
   *
   * @param props Properties where to search and set for the custom implementation of CacheKeysFactory
   */
  private void prepareCacheKeysFactory(Properties props) {
    String factoryName = props.getProperty(Environment.CACHE_KEYS_FACTORY, null);
    if (factoryName == null || "default".equalsIgnoreCase(factoryName)){
      cacheKeysFactory = DefaultCacheKeysFactory.INSTANCE;
      return;
    }
    if ("simple".equalsIgnoreCase(factoryName)){
      cacheKeysFactory = SimpleCacheKeysFactory.INSTANCE;
    }
    try {
      Class<?> clazz = Class.forName(factoryName);
      if (CacheKeysFactory.class.isAssignableFrom(CacheKeysFactory.class)){
        cacheKeysFactory = (CacheKeysFactory) clazz.newInstance();
      }
    }catch (Throwable t){
      log.warn("CacheKeysFactory class {} can't be instantiated. Defaulting to DefaultCacheKeysFactory", factoryName);
    }
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
                                 options,
                                 metadata,
                                 properties, cacheKeysFactory);
  }

  @Override
  public NaturalIdRegion buildNaturalIdRegion(String regionName,
                                              Properties properties,
                                              CacheDataDescription metadata) throws CacheException {
    return new RedisNaturalIdRegion(accessStrategyFactory,
                                    redis,
                                    this,
                                    regionName,
                                    options,
                                    metadata,
                                    properties, cacheKeysFactory);
  }

  @Override
  public CollectionRegion buildCollectionRegion(String regionName,
                                                Properties properties,
                                                CacheDataDescription metadata) throws CacheException {
    return new RedisCollectionRegion(accessStrategyFactory,
                                     redis,
                                     this,
                                     regionName,
                                     options,
                                     metadata,
                                     properties, cacheKeysFactory);
  }

  @Override
  public QueryResultsRegion buildQueryResultsRegion(String regionName,
                                                    Properties properties) throws CacheException {
    return new RedisQueryResultsRegion(accessStrategyFactory,
                                       redis,
                                       this,
                                       regionName,
                                       properties, cacheKeysFactory);
  }

  @Override
  public TimestampsRegion buildTimestampsRegion(String regionName,
                                                Properties properties) throws CacheException {
    return new RedisTimestampsRegion(accessStrategyFactory,
                                     redis,
                                     this,
                                     regionName,
                                     properties, cacheKeysFactory);
  }

  private static final long serialVersionUID = 4244155609146774509L;
}
