package org.hibernate.cache.redis.strategy;

import org.hibernate.cache.redis.hibernate4.regions.RedisTransactionalDataRegion;
import org.hibernate.cache.redis.hibernate4.strategy.AbstractReadWriteRedisAccessStrategy;
import org.hibernate.cfg.Settings;

public class ItemValueExtractor extends AbstractReadWriteRedisAccessStrategy<RedisTransactionalDataRegion> {

  public ItemValueExtractor(RedisTransactionalDataRegion region, Settings settings) {
    super(region, settings);
  }

  @SuppressWarnings("unchecked")
  public static <T> T getValue(final Object entry) {
    return (T) entry;
  }
}
