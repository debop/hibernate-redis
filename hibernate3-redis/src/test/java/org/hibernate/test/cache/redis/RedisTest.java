package org.hibernate.test.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cache.ReadWriteCache;
import org.hibernate.cache.redis.RedisRegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.testing.cache.BaseCacheTestCase;
import org.hibernate.testing.cache.Item;
import org.hibernate.testing.cache.VersionedItem;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: stadia
 * Date: 13. 5. 14
 * Time: 오후 2:18
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public abstract class RedisTest extends BaseCacheTestCase {

    public RedisTest(String x) {
        super(x);
    }

    @Override
    protected void configCache(final Configuration cfg) {
        cfg.setProperty(Environment.CACHE_REGION_FACTORY, getCacheRegionFactory().getName());

        if (getConfigResourceKey() != null) {
            cfg.setProperty(getConfigResourceKey(), getConfigResourceLocation());
        }
    }

    @Override
    public String getCacheConcurrencyStrategy() {
        return "read-write";
    }

    protected Class getCacheRegionFactory() {
        return RedisRegionFactory.class;
    }

    protected abstract Map getMapFromCachedEntry(Object entry);

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
        i.setDescription("A really top-quality, full-featured widget.");
        s.persist(i);
        t.commit();
        s.close();

        SecondLevelCacheStatistics slcs = s.getSessionFactory().getStatistics()
                .getSecondLevelCacheStatistics(Item.class.getName());

        assertEquals( slcs.getPutCount(), 1 );
        assertEquals( slcs.getElementCountInMemory(), 1 );
        assertEquals( slcs.getEntries().size(), 1 );

        s = openSession();
        t = s.beginTransaction();
        i = (Item) s.get( Item.class, i.getId() );

        assertNotNull(i);
        assertEquals("widget", i.getName());

        //assertEquals( slcs.getHitCount(), 1 );
        //assertEquals( slcs.getMissCount(), 0 );

        i.setDescription("A bog standard item");

        t.commit();
        s.close();

        assertEquals(slcs.getPutCount(), 2);

        Object entry = slcs.getEntries().get(i.getId());
        Map map = getMapFromCachedEntry(entry);
        assertTrue(map.get("description").equals("A bog standard item"));
        assertTrue(map.get("name").equals("widget"));

        // cleanup
        s = openSession();
        t = s.beginTransaction();
        s.delete(i);
        t.commit();
        s.close();
    }

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
        item.setVersion(new Long(item.getVersion().longValue() - 1));

        try {
            s = openSession();
            txn = s.beginTransaction();
            s.update(item);
            txn.commit();
            s.close();
            fail("expected stale write to fail");
        } catch (Throwable expected) {
            // expected behavior here
            if (txn != null) {
                try {
                    txn.rollback();
                } catch (Throwable ignore) {
                }
            }
        } finally {
            if (s != null && s.isOpen()) {
                try {
                    s.close();
                } catch (Throwable ignore) {
                }
            }
        }

        // check the version value in the cache...
        SecondLevelCacheStatistics slcs = sfi().getStatistics()
                .getSecondLevelCacheStatistics(VersionedItem.class.getName());

        Object entry = slcs.getEntries().get(item.getId());
        Long cachedVersionValue;
        if (entry instanceof ReadWriteCache.Lock) {
            //FIXME don't know what to test here
            cachedVersionValue = new Long(((ReadWriteCache.Lock) entry).getUnlockTimestamp());
        } else if (entry.getClass().getName().equals("org.hibernate.cache.redis.strategy.AbstractReadWriteRedisAccessStrategy$Lock")) {
            //FIXME don't know what to test here, which is the same as issue as above...
        } else {
            cachedVersionValue = (Long) getMapFromCachedEntry(entry).get("_version");
            assertEquals(initialVersion.longValue(), cachedVersionValue.longValue());
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
