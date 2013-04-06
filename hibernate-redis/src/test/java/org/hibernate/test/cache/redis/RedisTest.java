package org.hibernate.test.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.Map;

/**
 * org.hibernate.test.cache.redis.RedisTest
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:51
 */
@Slf4j
public abstract class RedisTest extends BaseCoreFunctionalTestCase {

    @Override
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[]{ Item.class };
    }

    @Override
    public String getCacheConcurrencyStrategy() {
        return "read-write";
    }

    @Override
    protected void configure(Configuration cfg) {
        super.configure(cfg);
        cfg.setProperty(Environment.CACHE_REGION_PREFIX, "");
        cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
        cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
        cfg.setProperty(Environment.USE_STRUCTURED_CACHE, "true");
        configCache(cfg);
        cfg.setProperty(Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName());

    }

    protected abstract void configCache(final Configuration cfg);

    protected abstract Map getMapFromCacheEntry(final Object entry);


    @Test
    public void emptySecondLevelCacheEntry() throws Exception {
        sessionFactory().getCache().evictEntityRegion(Item.class.getName());
    }
}
