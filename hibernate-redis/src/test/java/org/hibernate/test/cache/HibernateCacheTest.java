package org.hibernate.test.cache;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.test.AbstractHibernateTest;
import org.hibernate.test.domain.Item;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

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

	@Test
	public void configurationTest() {
		assertThat(sessionFactory).isNotNull();
	}

	@Test
	public void cacheTest() {
		Item item = new Item();
		item.setName("redis");
		item.setDescription("redis cache item");

		Session session = sessionFactory.getCurrentSession();
		session.save(item);
		session.clear();

		Item loaded = (Item) session.get(Item.class, item.getId());
		assertThat(loaded).isNotNull();
		assertThat(sessionFactory.getCache().containsEntity(Item.class, item.getId())).isTrue();

		loaded.setDescription("Update description...");
		session.save(loaded);
		session.clear();

		loaded = (Item) session.get(Item.class, item.getId());
		assertThat(loaded).isNotNull();
		assertThat(sessionFactory.getCache().containsEntity(Item.class, item.getId())).isTrue();
	}
}
