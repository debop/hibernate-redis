package org.hibernate.test.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.RedisRegionFactory;
import org.hibernate.cache.redis.strategy.ItemValueExtractor;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Map;

/**
 * org.hibernate.test.cache.redis.RedisRegionTest
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:52
 */
@Slf4j
public class RedisRegionTest extends RedisTest {

    public RedisRegionTest(final String string) {super(string);}

    @Override
    protected Map getMapFromCachedEntry(final Object entry) {
        final Map map;
        if (entry.getClass()
                .getName()
                .equals("org.hibernate.cache.redis.strategy.AbstractReadWriteRedisAccessStrategy$Item")) {
            map = ItemValueExtractor.getValue(entry);
        } else {
            map = (Map) entry;
        }
        return map;
    }
}
