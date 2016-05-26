package org.hibernate.cache.redis.hibernate4;

import org.hibernate.cache.redis.strategy.ItemValueExtractor;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Map;

public class SingletonRedisRegionFactoryTest extends AbstractHibernateCacheTest {

  @Override
  protected void configCache(Configuration cfg) {

    cfg.setProperty(Environment.CACHE_REGION_FACTORY,
                    org.hibernate.cache.redis.hibernate4.SingletonRedisRegionFactory.class.getName());
    cfg.setProperty(Environment.CACHE_PROVIDER_CONFIG, "conf/hibernate-redis.properties");
  }

  @Override
  protected Map getMapFromCacheEntry(Object entry) {
    final Map map;

    if (entry.getClass()
             .getName()
             .equals(HIBERNATE_ITEM_CLASS_NAME)) {
      map = ItemValueExtractor.getValue(entry);
    } else {
      map = (Map) entry;
    }

    return map;
  }
}
