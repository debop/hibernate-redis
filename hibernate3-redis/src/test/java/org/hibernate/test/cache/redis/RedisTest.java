package org.hibernate.test.cache.redis;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cache.redis.RedisRegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.cache.BaseCacheRegionFactoryTestCase;
import org.hibernate.testing.cache.Item;
import org.hibernate.testing.cache.VersionedItem;
import org.hibernate.transaction.JDBCTransactionFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestRule;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.test.cache.redis.RedisTest
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:51
 */
@Slf4j
public abstract class RedisTest extends BaseCacheRegionFactoryTestCase {

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    public RedisTest(String x) {
        super(x);
    }

    @Override
    public String getCacheConcurrencyStrategy() {
        return "read-write";
    }

    @Override
    public void configure(Configuration cfg) {
        super.configure(cfg);
        cfg.setProperty(Environment.TRANSACTION_STRATEGY, JDBCTransactionFactory.class.getName());
	}

    @Override
    protected Class getCacheRegionFactory() {
        return RedisRegionFactory.class;
    }

    @Override
    protected String getConfigResourceKey() {
        return Environment.CACHE_PROVIDER_CONFIG;
    }

    @Override
    protected String getConfigResourceLocation() {
        return "redis.properties";
    }

    @Override
    protected boolean useTransactionManager() {
        return false;
    }

    @Override
    public void testQueryCacheInvalidation() {
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

        assertThat(slcs.getElementCountInMemory()).isGreaterThan(0);

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

    @Override
    public void testEmptySecondLevelCacheEntry() throws Exception {
        getSessions().getCache().evictEntityRegion(Item.class.getName());
        Statistics stats = getSessions().getStatistics();
        stats.clear();
        SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics(Item.class.getName());
        Map cacheEntries = statistics.getEntries();

        assertThat(cacheEntries).isNotNull();
        assertThat(cacheEntries.size()).isEqualTo(0);
    }

    @SuppressWarnings({ "UnnecessaryBoxing", "UnnecessaryUnboxing", "UnusedAssignment" })
    @Override
    public void testStaleWritesLeaveCacheConsistent() {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        VersionedItem item = new VersionedItem();
        item.setName("steve");
        item.setDescription("steve's item");
        s.save(item);
        txn.commit();
        s.close();

        Long initialVersion = item.getVersion();

        // manually revert the version property
        item.setVersion(Long.valueOf(item.getVersion().longValue() - 1));

        try {
            s = openSession();
            txn = s.beginTransaction();
            s.update(item);
            txn.commit();
            s.close();
            Assert.fail("expected stale write to fail");
        } catch (Throwable expected) {
            if (txn != null) {
                try {
                    txn.rollback();
                } catch (Throwable ignore) {}
            }
        } finally {
            if (s != null && s.isOpen()) {
                try {
                    s.close();
                } catch (Throwable ignore) {}
            }
        }

        // check the version value in the cache...
		SecondLevelCacheStatistics slcs = getSessions().getStatistics()
                .getSecondLevelCacheStatistics(VersionedItem.class.getName());

        Map cacheEntries = slcs.getEntries();
        Object entry = cacheEntries.get(item.getId());

        log.debug("entry=[{}]", entry);

        Long cachedVersionValue;

        boolean isLock = entry.getClass()
                              .getName()
                              .equals("org.hibernate.cache.redis.strategy.AbstractReadWriteRedisAccessStrategy$Lock");
        if (isLock) {
            //
        } else {
			cachedVersionValue = (Long) getMapFromCachedEntry(entry).get("_version");
            assertThat(cachedVersionValue.longValue()).isEqualTo(initialVersion.longValue());
        }

        // cleanup
        s = openSession();
        txn = s.beginTransaction();
        item = (VersionedItem) s.load(VersionedItem.class, item.getId());
        s.delete(item);
        txn.commit();
        s.close();
    }
}
