package org.hibernate.cache.redis.client;

import java.util.Properties;

/**
 * @author debop sunghyouk.bae@gmail.com
 */
public final class RedisClientFactory {

  private RedisClientFactory() {
  }

  /**
   * Create {@link RedisClient} instance by properties
   *
   * @param props properties for redis server settings
   * @return {@link RedisClient} instance
   */
  public static RedisClient createRedisClient(Properties props) {
    // TODO: implementation
    //return RedisClient(...);
    return null;
  }
}
