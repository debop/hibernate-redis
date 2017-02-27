/*
 * Copyright (c) 2017. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.cache.redis.hibernate5;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cache.redis.hibernate5.models.HibernateItem;
import org.hibernate.cache.redis.hibernate5.models.VersionedHibernateItem;
import org.hibernate.cache.redis.hibernate5.strategy.AbstractReadWriteRedisAccessStrategy;
import org.hibernate.cache.redis.hibernate5.util.HibernateCacheUtil;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorBuilderImpl;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hibernate.cfg.Environment.*;

@Slf4j
public abstract class AbstractHibernateCacheTest extends BaseCoreFunctionalTestCase {

  protected static final String ABSTRACT_READ_WRITE_REDIS_ACCESS_STRATEGY_CLASS_NAME =
      AbstractReadWriteRedisAccessStrategy.class.getName();

  protected static final String HIBERNATE_ITEM_CLASS_NAME =
      ABSTRACT_READ_WRITE_REDIS_ACCESS_STRATEGY_CLASS_NAME + "$HibernateItem";

  @Override
  protected Class<?>[] getAnnotatedClasses() {
    return new Class<?>[]{
        HibernateItem.class,
        VersionedHibernateItem.class
    };
  }

  @Override
  protected String getCacheConcurrencyStrategy() {
    return AccessType.READ_WRITE.getExternalName();
  }

  @Override
  protected void configure(Configuration cfg) {
    super.configure(cfg);

    cfg.setProperty(CACHE_REGION_PREFIX, "hibernate");
    cfg.setProperty(USE_SECOND_LEVEL_CACHE, "true");
    cfg.setProperty(USE_QUERY_CACHE, "true");
    cfg.setProperty(GENERATE_STATISTICS, "true");
    cfg.setProperty(USE_STRUCTURED_CACHE, "true");

    cfg.setProperty(TRANSACTION_COORDINATOR_STRATEGY, JtaTransactionCoordinatorBuilderImpl.class.getName());

    configCache(cfg);
  }


  protected abstract void configCache(final Configuration cfg);

  protected abstract Map getMapFromCacheEntry(final Object entry);

  @Test
  public void queryCacheInvalidation() {
    Session s = openSession();
    Transaction t = s.beginTransaction();

    HibernateItem item = new HibernateItem();
    item.setName("widget");
    item.setDescription("A really top-quality, full-featured widget");
    s.persist(item);
    t.commit();
    s.clear();

    String regionName = HibernateCacheUtil.getRegionName(sessionFactory(), HibernateItem.class);
    assertThat(regionName).isNotEmpty();
    log.debug("HibernateItem regionName={}", regionName);
    SecondLevelCacheStatistics slcs = sessionFactory().getStatistics()
        .getSecondLevelCacheStatistics(regionName);

    // FIXME : HibernateItem Region Name 이 지정한 값이 아닌 class name 으로 나온다. Why???
    //assertThat(slcs.getElementCountInMemory()).isGreaterThan(0);

    s = openSession();
    t = s.beginTransaction();
    HibernateItem loaded = (HibernateItem) s.get(HibernateItem.class, item.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getId()).isEqualTo(item.getId());
    assertThat(loaded.getName()).isEqualTo(item.getName());
    assertThat(loaded.getDescription()).isEqualTo(item.getDescription());

    loaded.setDescription("A blog standard item");

    t.commit();
    s.close();

    s = openSession();
    t = s.beginTransaction();
    s.delete(item);
    t.commit();
    s.close();
  }

  @Test
  public void emptySecondLevelCacheEntry() throws Exception {
    SessionFactory sf = sessionFactory();
    sf.getCache().evictEntityRegion(HibernateItem.class.getName());
    Statistics stats = sf.getStatistics();

    String regionName = HibernateCacheUtil.getRegionName(sf, HibernateItem.class);
    log.debug("regionName={}", regionName);
    assertThat(regionName).isNotEmpty();

    stats.clear();
    SecondLevelCacheStatistics slcs = stats.getSecondLevelCacheStatistics(regionName);
    Map cacheEntries = slcs.getEntries();

    assertThat(cacheEntries).isNotNull().isEmpty();
  }

  @SuppressWarnings("all")
  @Test
  public void staleWritesLeaveCacheConsistent() {
    Session session = openSession();
    Transaction tx = session.beginTransaction();
    VersionedHibernateItem vitem = new VersionedHibernateItem();
    vitem.setName("steve");
    vitem.setDescription("steve's item");
    session.save(vitem);
    tx.commit();
    session.close();

    Long initialVersion = vitem.getVersion();

    // manually revert the version property
    vitem.setVersion(Long.valueOf(vitem.getVersion().longValue() - 1L));

    try {
      session = openSession();
      tx = session.beginTransaction();
      session.update(vitem);
      tx.commit();
      session.close();
      fail("expected stale write to fail!!!");
    } catch (Exception expected) {
      if (tx != null) {
        try {
          tx.rollback();
        } catch (Exception ignored) {
        }
      }
    } finally {
      if (session != null && session.isOpen()) {
        try {
          session.close();
        } catch (Exception ignored) {
        }
      }
    }


    // check the version value in the cache...
    String regionName = HibernateCacheUtil.getRegionName(sessionFactory(),
        VersionedHibernateItem.class);
    log.debug("VersionedHibernateItem's regionName={}", regionName);
    assertThat(regionName).isNotEmpty();

    SecondLevelCacheStatistics slcs = sessionFactory().getStatistics()
        .getSecondLevelCacheStatistics(regionName);

//    Map cacheEntries = slcs.getEntries();
//    Object entry = cacheEntries.get(vitem.getId());
//    log.debug("entry=[{}]", entry);

    Long cachedVersionValue;

//    final String lockStr = AbstractReadWriteRedisAccessStrategy.class.getName() + "$Lock";
//    boolean isLock = entry.getClass()
//                          .getName()
//                          .equals(lockStr);
//
//    if (isLock) {
//      //
//    } else {
//      cachedVersionValue = (Long) getMapFromCacheEntry(entry).get("_version");
//      assertThat(cachedVersionValue.longValue()).isEqualTo(initialVersion.longValue());
//    }

    // clean up
    session = openSession();
    tx = session.beginTransaction();
    VersionedHibernateItem loaded = (VersionedHibernateItem) session.load(VersionedHibernateItem.class, vitem.getId());
    assertThat(loaded).isNotNull();
    session.delete(loaded);
    tx.commit();
    session.close();
  }
}
