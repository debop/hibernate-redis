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

package org.hibernate.cache.redis.spring;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cache.redis.hibernate4.util.HibernateCacheUtil;
import org.hibernate.cache.redis.jpa.models.Account;
import org.hibernate.cache.redis.jpa.models.Item;
import org.hibernate.cache.redis.jpa.models.Person;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HibernateRedisConfiguration.class})
public class SpringHibernateTest {

  @Autowired
  SessionFactory sf;

  @Before
  public void setup() {
    assertThat(sf).isNotNull();

    sf.getStatistics().setStatisticsEnabled(true);
    sf.getStatistics().clear();
  }

  public SecondLevelCacheStatistics getSecondLevelCacheStatistics(Class clazz) {
    String regionName = HibernateCacheUtil.getRegionName(sf, clazz);
    log.debug("class={}, region={}", clazz.getSimpleName(), regionName);

    return sf.getStatistics().getSecondLevelCacheStatistics(regionName);
  }

  @Test
  public void emptySecondLevelCacheEntry() throws Exception {
    sf.getCache().evictEntityRegion(Item.class);
    Statistics stats = sf.getStatistics();
    stats.clear();

    String regionName = HibernateCacheUtil.getRegionName(sf, Item.class);
    assertThat(regionName).isNotEmpty();
    log.debug("Item region name={}", regionName);
    SecondLevelCacheStatistics slcs = getSecondLevelCacheStatistics(Item.class);

    log.debug("Region name={}, Element in memory={}, hit count={}",
              regionName, slcs.getElementCountInMemory(), slcs.getHitCount());

    assertThat(regionName).contains("common");
    assertThat(slcs.getElementCountInMemory()).isEqualTo(0);
    assertThat(slcs.getHitCount()).isEqualTo(0);
  }

  @Test
  public void queryCacheInvalidation() throws Exception {
    sf.getCache().evictEntityRegion(Item.class);

    Session s = sf.openSession();

    Transaction tx = s.beginTransaction();
    Item item = Item.of("Widget", "A realy top-quality, full-featured widget.");
    s.persist(item);
    tx.commit();
    s.close();

    SecondLevelCacheStatistics slcs = getSecondLevelCacheStatistics(Item.class);
    log.info(slcs.toString());

    s = sf.openSession();
    Item loaded = (Item) s.get(Item.class, item.getId());
    assertThat(loaded).isNotNull();
    s.close();

    log.info(slcs.toString());
    assertThat(slcs.getPutCount()).isEqualTo(1);
    assertThat(slcs.getElementCountInMemory()).isEqualTo(1);

  }

  @Test
  public void simpleEntityCaching() throws Exception {
    sf.getCache().evictEntityRegion(Item.class);

    SecondLevelCacheStatistics slcs = getSecondLevelCacheStatistics(Item.class);
    Session session;

    log.debug("Item Save - #1");
    Item item = new Item();
    item.setName("redis");
    item.setDescription("redis cache item");

    session = sf.openSession();
    session.save(item);
    session.flush();
    session.close();

    log.debug("Item Read - #1");
    session = sf.openSession();
    Item loaded = (Item) session.get(Item.class, item.getId());
    assertThat(loaded).isNotNull();
    session.close();

    log.info(slcs.toString());
    log.debug("put count={}, element in memory={}", slcs.getPutCount(), slcs.getElementCountInMemory());
    assertThat(slcs.getPutCount()).isEqualTo(1);
    assertThat(slcs.getElementCountInMemory()).isEqualTo(1);


    log.debug("Item Update - #1");
    session = sf.openSession();
    // session.merge(loaded);
    loaded.setDescription("Update description...");
    session.saveOrUpdate(loaded);
    session.flush();
    session.close();

    log.debug("Item Read - #2");
    session = sf.openSession();
    loaded = (Item) session.get(Item.class, loaded.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getId()).isEqualTo(item.getId());
    assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
    session.close();

    log.debug("Item Read - #3");
    session = sf.openSession();
    loaded = (Item) session.get(Item.class, loaded.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getId()).isEqualTo(item.getId());
    assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
    session.close();

    log.debug("Item Read - #4");
    session = sf.openSession();
    loaded = (Item) session.get(Item.class, loaded.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getId()).isEqualTo(item.getId());
    assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
    session.close();

    log.info(slcs.toString());
    log.debug("put count={}, element in memory={}", slcs.getPutCount(), slcs.getElementCountInMemory());
    assertThat(slcs.getPutCount()).isEqualTo(2);  // update
    assertThat(slcs.getElementCountInMemory()).isEqualTo(1);
  }

  @Test
  public void hqlLoad() throws Exception {
    sf.getCache().evictEntityRegion(Item.class);
    SecondLevelCacheStatistics slcs = getSecondLevelCacheStatistics(Item.class);
    Session session;

    log.debug("Item Save - #1");
    Item item = Item.of("redis", "redis cache item");

    session = sf.openSession();
    session.save(item);
    session.flush();
    session.close();

    log.debug("Item Read - #1");
    session = sf.openSession();
    Query query = session.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setCacheable(true);
    Item loaded = (Item) query.uniqueResult();
    assertThat(loaded).isNotNull();
    session.close();

    log.debug("Item Read - #2");
    session = sf.openSession();
    query = session.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setCacheable(true);
    loaded = (Item) query.uniqueResult();
    assertThat(loaded).isNotNull();
    session.close();

    log.debug("Item Read - #3");
    session = sf.openSession();
    loaded = (Item) session.get(Item.class, item.getId());
    assertThat(loaded).isNotNull();
    session.close();

    log.debug("Item Read - #4");
    session = sf.openSession();
    query = session.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setCacheable(true);
    loaded = (Item) query.uniqueResult();
    assertThat(loaded).isNotNull();
    session.close();
  }

  @Test
  public void nonrestrictCaching() throws Exception {
    sf.getCache().evictEntityRegion(Account.class);
    sf.getCache().evictEntityRegion(Person.class);

    SecondLevelCacheStatistics slcs = getSecondLevelCacheStatistics(Account.class);
    SecondLevelCacheStatistics slcs2 = getSecondLevelCacheStatistics(Person.class);

    Session session = sf.openSession();

    Person person = new Person();
    person.setAge(40);
    person.setFirstname("Bae");
    person.setLastname("Sunghyouk");
    session.save(person);

    Account account = new Account();
    account.setPerson(person);
    session.save(account);

    session.flush();
    session.close();

    session = sf.openSession();
    Account acc = (Account) session.get(Account.class, account.getId());
    assertThat(acc).isNotNull();
    assertThat(acc.getPerson()).isNotNull();
    session.close();

    log.info("Account:[{}]", slcs.toString());
    assertThat(slcs.getPutCount()).isEqualTo(1);
    assertThat(slcs.getElementCountInMemory()).isEqualTo(1);

    log.info("Person:[{}]", slcs2.toString());
    assertThat(slcs2.getPutCount()).isEqualTo(1);
    assertThat(slcs2.getElementCountInMemory()).isEqualTo(1);

    session = sf.openSession();
    acc = (Account) session.get(Account.class, account.getId());
    assertThat(acc).isNotNull();
    assertThat(acc.getPerson()).isNotNull();
    session.close();

    log.info("Account:[{}]", slcs.toString());
    assertThat(slcs.getPutCount()).isEqualTo(1);
    assertThat(slcs.getElementCountInMemory()).isEqualTo(1);

    log.info("Person:[{}]", slcs2.toString());
    assertThat(slcs2.getPutCount()).isEqualTo(1);
    assertThat(slcs2.getElementCountInMemory()).isEqualTo(1);
  }

  @Test
  public void massiveCaching() throws Exception {
    sf.getCache().evictEntityRegion(Item.class);

    Session session = sf.openSession();
    Transaction tx = session.beginTransaction();

    int count = 100;

    for (int i = 0; i < count; i++) {
      Item item = new Item();
      item.setName("redis - " + i);
      item.setDescription("redis cache item - " + i);
      session.save(item);
    }
    tx.commit();
    session.close();

    session = sf.openSession();
    tx = session.beginTransaction();
    List<Item> items = (List<Item>) session.createCriteria(Item.class).list();

    assertThat(items.size()).isEqualTo(count);

    items = (List<Item>) session.createCriteria(Item.class).list();

    for (Item item : items) {
      session.update(item);
    }
    tx.commit();
    session.close();

    SecondLevelCacheStatistics slcs = getSecondLevelCacheStatistics(Item.class);
    log.info(slcs.toString());
    assertThat(slcs.getPutCount()).isEqualTo(count);
    assertThat(slcs.getElementCountInMemory()).isEqualTo(count);

    session = sf.openSession();
    tx = session.beginTransaction();
    items = (List<Item>) session.createCriteria(Item.class).list();
    for (Item item : items) {
      item.getName();
    }
    tx.commit();
    session.close();

    session = sf.openSession();
    tx = session.beginTransaction();
    items = (List<Item>) session.createCriteria(Item.class).list();
    for (Item item : items) {
      session.delete(item);
    }
    tx.commit();
    session.flush();
    session.close();
  }
}
