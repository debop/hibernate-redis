package org.hibernate.test.cache;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.hibernate.test.AbstractHibernateTest;
import org.hibernate.test.domain.Account;
import org.hibernate.test.domain.Item;
import org.hibernate.test.domain.Person;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.test.cache.HibernateCacheTest
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:51
 */
@Slf4j
@Transactional
public class HibernateCacheTest extends AbstractHibernateTest {

    private String REGION_NAME = "hibernate-redis";

    @Before
    public void before() {
        sessionFactory.getStatistics().setStatisticsEnabled(true);
    }

    public SecondLevelCacheStatistics getSecondLevelCacheStatistics() {
        return sessionFactory.getStatistics().getSecondLevelCacheStatistics(REGION_NAME);
    }

    @Test
    public void emptySecondLevelCacheEntry() throws Exception {
        sessionFactory.getCache().evictEntityRegion(Item.class);
        Statistics stats = sessionFactory.getStatistics();
        stats.clear();


        SecondLevelCacheStatistics statistics = stats.getSecondLevelCacheStatistics(REGION_NAME);
        log.info("SecondLevel Cache Region=[{}], ElementInMemory=[{}], HitCount=[{}]",
                 REGION_NAME, statistics.getElementCountInMemory(), statistics.getHitCount());
    }

    @Test
    public void queryCacheInvalidation() throws Exception {
        Session s = sessionFactory.openSession();
        Transaction t = s.beginTransaction();
        Item i = new Item();
        i.setName("Widget");
        i.setDescription("A realy top-quality, full-featured widget.");
        s.persist(i);
        t.commit();
        s.close();

        SecondLevelCacheStatistics slcs = getSecondLevelCacheStatistics();

        // assertThat(slcs.getPutCount()).isEqualTo(1);
        assertThat(slcs.getElementCountInMemory()).isEqualTo(1);
        assertThat(slcs.getEntries().size()).isEqualTo(1);

    }

    @Test
    public void simpleEntityCaching() {
        Item item = new Item();
        item.setName("redis");
        item.setDescription("redis cache item");

        Session session = sessionFactory.getCurrentSession();
        session.save(item);
        session.flush();
        session.clear();

        Item loaded = (Item) session.get(Item.class, item.getId());
        assertThat(loaded).isNotNull();
        // assertThat(sessionFactory.getCache().containsEntity(Item.class, item.getId())).isTrue();

        loaded.setDescription("Update description...");
        session.save(loaded);
        session.flush();
        session.clear();

        loaded = (Item) session.get(Item.class, item.getId());
        assertThat(loaded).isNotNull();
        assertThat(sessionFactory.getCache().containsEntity(Item.class, item.getId())).isTrue();
    }

    @Test
    public void nonrestrictCaching() {
        Session session = sessionFactory.getCurrentSession();

        Person person = new Person();
        person.setAge(40);
        person.setFirstname("Bae");
        person.setLastname("Sunghyouk");
        session.saveOrUpdate(person);

        Account account = new Account();
        account.setPerson(person);
        session.saveOrUpdate(account);

        session.flush();
        session.clear();

        Account acc = (Account) session.get(Account.class, account.getId());
        assertThat(acc).isNotNull();
        assertThat(acc.getPerson()).isNotNull();

        session.evict(acc);
        acc = (Account) session.get(Account.class, account.getId());
        assertThat(acc).isNotNull();
        assertThat(acc.getPerson()).isNotNull();

        assertThat(sessionFactory.getCache().containsEntity(Account.class, account.getId())).isTrue();
        assertThat(sessionFactory.getCache().containsEntity(Person.class, person.getId())).isTrue();
    }

    @Test
    public void massiveCaching() {
        Session session = sessionFactory.getCurrentSession();
        int count = 100;

        for (int i = 0; i < count; i++) {
            Item item = new Item();
            item.setName("redis - " + i);
            item.setDescription("redis cache item - " + i);
            session.save(item);
        }
        session.flush();

        List<Item> items = (List<Item>) session.createCriteria(Item.class).list();

        assertThat(items.size()).isEqualTo(count);
        for (Item item : items) {
            assertThat(sessionFactory.getCache().containsEntity(Item.class, item.getId())).isTrue();
        }

        sessionFactory.getCache().evictEntityRegion(Item.class);
    }
}
