package org.hibernate.test.cache.redis;

import org.hibernate.cache.redis.RedisRegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * org.hibernate.test.cache.redis.RedisRegionFactoryImpl
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:52
 */
public class RedisRegionFactoryImpl extends RedisTest {

    @Override
    protected void configCache(Configuration cfg) {
        cfg.setProperty(Environment.CACHE_REGION_FACTORY, RedisRegionFactory.class.getName());
        cfg.setProperty(Environment.CACHE_PROVIDER_CONFIG, "redis.properties");
    }

    @Override
    protected Map getMapFromCacheEntry(final Object entry) {
        final Map map;
        if (entry.getClass()
                .getName()
                .equals("org.hibernate.cache.redis.strategy.AbstractReadWriteRedisAccessStrategy$Item")) {
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
