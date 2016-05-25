package org.hibernate.cache.redis.hibernate5;


import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.lang.reflect.Field;
import java.util.Map;

public class RedisRegionFactoryTest extends AbstractHibernateCacheTest {

  @Override
  protected void configCache(Configuration cfg) {
    cfg.setProperty(Environment.CACHE_REGION_FACTORY,
        org.hibernate.cache.redis.hibernate5.RedisRegionFactory.class.getName());
    cfg.setProperty(Environment.CACHE_PROVIDER_CONFIG, "conf/hibernate-redis.properties");
  }

  @Override
  protected Map getMapFromCacheEntry(Object entry) {
    final Map map;
    if (entry.getClass()
        .getName()
        .equals(HIBERNATE_ITEM_CLASS_NAME)) {
      try {
        Field field = entry.getClass().getDeclaredField("value");
        field.setAccessible(true);
        map = (Map) field.get(entry);
      } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }

    } else {
      map = (Map) entry;
    }
    return map;
  }
}
