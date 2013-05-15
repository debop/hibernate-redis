package org.hibernate.test.cache.redis;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * org.hibernate.test.cache.redis.RedisRegionFactoryImpl
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:52
 */
public class RedisRegionFactoryImpl extends RedisTest {

    public RedisRegionFactoryImpl(String x) {
        super(x);
    }

    @Override
    protected Map getMapFromCachedEntry(Object entry) {
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
