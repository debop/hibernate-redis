package org.hibernate.test.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

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
    public void queryCacheInvalidation() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Item i = new Item();
        i.setName("widget");
        i.setDescription("A really top-quality, full-featured widget");
        s.persist(i);
        t.commit();
        s.clear();

        SecondLevelCacheStatistics slcs = s.getSessionFactory().getStatistics()
                .getSecondLevelCacheStatistics(Item.class.getName());
        assertThat(slcs.getElementCountInMemory()).isEqualTo(1);

        s = openSession();
        t = s.beginTransaction();
        i = (Item) s.get(Item.class, i.getId());

        assertThat(i).isNotNull();
        assertThat(i.getName()).isEqualTo("widget");

        i.setDescription("A blog standard item");

        t.commit();
        s.close();

        s = openSession();
        t = s.beginTransaction();
        s.delete(i);
        t.commit();
        s.close();
    }

    @Test
    public void emptySecondLevelCacheEntry() throws Exception {
        sessionFactory().getCache().evictEntityRegion(Item.class.getName());
    }
}
