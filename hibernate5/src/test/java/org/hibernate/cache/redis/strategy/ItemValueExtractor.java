package org.hibernate.cache.redis.strategy;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.redis.hibernate5.regions.RedisTransactionalDataRegion;
import org.hibernate.cache.redis.hibernate5.strategy.AbstractReadWriteRedisAccessStrategy;

public class ItemValueExtractor extends AbstractReadWriteRedisAccessStrategy<RedisTransactionalDataRegion> {

  public ItemValueExtractor(RedisTransactionalDataRegion region, SessionFactoryOptions options) {
    super(region, options);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getValue(final Object entry) {
    return (T) entry;
  }
}
