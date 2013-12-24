package org.hibernate.test.ehcache;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.test.domain.Event;
import org.hibernate.test.domain.Item;
import org.hibernate.test.jpa.repository.EventRepository;
import org.hibernate.test.jpa.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.test.ehcache.JpaEhcacheTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오전 9:10
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaEhcacheConfiguration.class })
@Transactional
public class JpaEhcacheTest {

    @PersistenceContext EntityManager em;
    @Autowired EventRepository eventRepository;

    @Before
    public void clearItems() throws Exception {
        itemRepository.deleteAll();
        itemRepository.flush();
        em.clear();
        em.getEntityManagerFactory().getCache().evict(Item.class);
    }

    @Test
    public void configurationTest() throws Exception {
        assertThat(em).isNotNull();
        assertThat(eventRepository).isNotNull();
    }

    @Test
    public void loadEventByTitle() throws Exception {
        List<Event> events = eventRepository.findByTitle("abc");
        assertThat(events).isNotNull();
    }

    @Test
    public void emptySecondLevelCacheEntry() throws Exception {
        em.getEntityManagerFactory().getCache().evict(Item.class);
    }

    @Test
    @Rollback(false)
    public void queryCacheInvalidation() throws Exception {
        Item i = new Item();
        i.setName("Widget");
        i.setDescription("A realy top-quality, full-featured widget.");
        em.persist(i);
        em.flush();
        em.clear();

        Item loaded = (Item) em.find(Item.class, i.getId());
        assertThat(loaded).isNotNull();

        em.clear();

        loaded = (Item) em.find(Item.class, i.getId());
        assertThat(loaded).isNotNull();
    }

    @Test
    @Rollback(false)
    public void simpleEntityCaching() {

        log.debug("Item 저장 - #1");
        Item item = new Item();
        item.setName("redis");
        item.setDescription("redis cache item");
        em.persist(item);
        em.flush();
        em.clear();

        log.debug("Item 조회 - #1");
        Item loaded = (Item) em.find(Item.class, item.getId());
        assertThat(loaded).isNotNull();

        log.debug("Item Update - #1");
        // session.merge(loaded);
        loaded.setDescription("Update description...");
        em.persist(loaded);
        em.flush();
        em.clear();

        log.debug("Item 조회 - #2");
        loaded = (Item) em.find(Item.class, item.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded.getId()).isEqualTo(item.getId());
        assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
        em.clear();

        log.debug("Item 조회 - #3");
        loaded = (Item) em.find(Item.class, item.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded.getId()).isEqualTo(item.getId());
        assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
        em.clear();

        log.debug("Item 조회 - #4");
        loaded = (Item) em.find(Item.class, item.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded.getId()).isEqualTo(item.getId());
        assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
        em.clear();
    }

    @Test
    @Rollback(false)
    public void hqlLoad() throws Exception {

        log.debug("Item 저장 - #1");

        Item item = new Item();
        item.setName("redis");
        item.setDescription("redis cache item");
        em.persist(item);
        em.flush();
        em.clear();

        log.debug("Item 조회 - #1");

        //NOTE: http://docs.jboss.org/hibernate/stable/entitymanager/reference/en/html/objectstate.html#d0e1215

        Query query = em.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setHint("org.hibernate.cacheable", true);
        Item loaded = (Item) query.getSingleResult();
        assertThat(loaded).isNotNull();
        em.clear();

        log.debug("Item 조회 - #2");
        query = em.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setHint("org.hibernate.cacheable", true);
        loaded = (Item) query.getSingleResult();
        assertThat(loaded).isNotNull();
        em.clear();

        log.debug("Item 조회 - #3");
        loaded = (Item) em.find(Item.class, item.getId());
        assertThat(loaded).isNotNull();
        em.clear();

        log.debug("Item 조회 - #4");
        query = em.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setHint("org.hibernate.cacheable", true);
        loaded = (Item) query.getSingleResult();
        assertThat(loaded).isNotNull();
        em.clear();
    }


    @Autowired ItemRepository itemRepository;

    @Test
    public void springRepositoryTest() throws Exception {

        log.debug("Item 저장 - #1");

        Item item = new Item();
        item.setName("redis");
        item.setDescription("redis cache item");
        em.persist(item);
        em.flush();
        em.clear();

        log.debug("Item 조회 - #1");

        List<Item> items = itemRepository.findByName("redis");
        assertThat(items).isNotNull();
        assertThat(items).hasSize(1);

        log.debug("Item 조회 - #2");

        items = itemRepository.findByName("redis");
        assertThat(items).isNotNull();
        assertThat(items).hasSize(1);
    }
}
