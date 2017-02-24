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

package org.hibernate.cache.redis.jpa;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.jpa.models.Item;
import org.hibernate.cache.redis.jpa.repository.EventRepository;
import org.hibernate.cache.redis.jpa.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JpaCacheConfiguration.class})
@Transactional(rollbackFor = {})
public class JpaCacheTest {

  @PersistenceContext EntityManager em;
  @Autowired EventRepository eventRepository;
  @Autowired ItemRepository itemRepository;

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

//    @Test
//    public void loadEventByTitle() throws Exception {
//        List<Event> events = eventRepository.findByTitle("abc");
//        assertThat(events).isNotNull();
//    }

  @Test
  public void emptySecondLevelCacheEntry() throws Exception {
    em.getEntityManagerFactory().getCache().evict(Item.class);
  }

  @Test
  @Rollback(false)
  public void queryCacheInvalidation() throws Exception {
    Item item = Item.of("Widget", "A realy top-quality, full-featured widget.");
    em.persist(item);
    em.flush();
    em.clear();

    Item loaded = (Item) em.find(Item.class, item.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getName()).isEqualTo(item.getName());

    em.clear();

    loaded = (Item) em.find(Item.class, item.getId());
    assertThat(loaded).isNotNull();
  }

  @Test
  @Rollback(false)
  public void simpleEntityCaching() {

    log.debug("Item Save - #1");
    Item item = Item.of("redis", "redis cache item");
    em.persist(item);
    em.flush();
    em.clear();

    log.debug("Item Read - #1");
    Item loaded = (Item) em.find(Item.class, item.getId());
    assertThat(loaded).isNotNull();

    log.debug("Item Update - #1");
    // session.merge(loaded);
    loaded.setDescription("Update description...");
    em.persist(loaded);
    em.flush();
    em.clear();

    log.debug("Item Read - #2");
    loaded = (Item) em.find(Item.class, item.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getId()).isEqualTo(item.getId());
    assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
    em.clear();

    log.debug("Item Read - #3");
    loaded = (Item) em.find(Item.class, item.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getId()).isEqualTo(item.getId());
    assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
    em.clear();

    log.debug("Item Read - #4");
    loaded = (Item) em.find(Item.class, item.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getId()).isEqualTo(item.getId());
    assertThat(loaded.getDescription()).isNotEqualTo(item.getDescription());
    em.clear();
  }

  @Test
  @Rollback(false)
  public void hqlLoad() throws Exception {

    log.debug("Item Save - #1");

    Item item = Item.of("redis", "redis cache item");
    em.persist(item);
    em.flush();
    em.clear();

    log.debug("Item Read - #1");

    //NOTE: http://docs.jboss.org/hibernate/stable/entitymanager/reference/en/html/objectstate.html#d0e1215

    Query query = em.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setHint("org.hibernate.cacheable", true);
    Item loaded = (Item) query.getSingleResult();
    assertThat(loaded).isNotNull();
    em.clear();

    log.debug("Item Read - #2");
    query = em.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setHint("org.hibernate.cacheable", true);
    loaded = (Item) query.getSingleResult();
    assertThat(loaded).isNotNull();
    em.clear();

    log.debug("Item Read - #3");
    loaded = (Item) em.find(Item.class, item.getId());
    assertThat(loaded).isNotNull();
    em.clear();

    log.debug("Item Read - #4");
    query = em.createQuery("select e from Item e where e.id=:id").setParameter("id", item.getId()).setHint("org.hibernate.cacheable", true);
    loaded = (Item) query.getSingleResult();
    assertThat(loaded).isNotNull();
    em.clear();
  }


  @Test
  public void springRepositoryTest() throws Exception {

    log.debug("Item Save - #1");

    Item item = Item.of("redis", "redis cache item");
    em.persist(item);
    em.flush();
    em.clear();

    log.debug("Item Read - #1");

    List<Item> items = itemRepository.findByName("redis");
    assertThat(items).isNotNull();
    assertThat(items).hasSize(1);

    log.debug("Item Read - #2");

    items = itemRepository.findByName("redis");
    assertThat(items).isNotNull();
    assertThat(items).hasSize(1);
  }
}
