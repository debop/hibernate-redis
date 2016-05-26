package org.hibernate.examples.mapping.associations.unidirectional;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.examples.AbstractHibernateTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.associations.unidirectional.CollectionUnidirectionalTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 4:21
 */
@Slf4j
@Transactional
public class CollectionUnidirectionalTest extends AbstractHibernateTest {

  private Session session;

  @Before
  public void before() {
    session = sessionFactory.getCurrentSession();
  }

  @Test
  public void unidirectionalCollection() throws Exception {

    SnowFlake sf = new SnowFlake();
    sf.setDescription("Snowflake 1");
    session.save(sf);

    SnowFlake sf2 = new SnowFlake();
    sf2.setDescription("Snowflake 2");
    session.save(sf2);

    Cloud cloud = new Cloud();
    cloud.setLength(23.0);
    cloud.getProducedSnowFlakes().add(sf);
    cloud.getProducedSnowFlakes().add(sf2);
    session.persist(cloud);
    session.flush();

    session.clear();

    cloud = (Cloud) session.get(Cloud.class, cloud.getId());
    assertThat(cloud.getProducedSnowFlakes()).isNotNull();
    assertThat(cloud.getProducedSnowFlakes()).hasSize(2);

    final SnowFlake removedSf = cloud.getProducedSnowFlakes().iterator().next();
    SnowFlake sf3 = new SnowFlake();
    sf3.setDescription("Snowflake 3");
    session.persist(sf3);

    cloud.getProducedSnowFlakes().remove(removedSf);
    cloud.getProducedSnowFlakes().add(sf3);

    session.flush();
    session.clear();

    cloud = (Cloud) session.get(Cloud.class, cloud.getId());
    assertThat(cloud.getProducedSnowFlakes()).isNotNull();
    assertThat(cloud.getProducedSnowFlakes()).hasSize(2);

    boolean present = false;
    for (SnowFlake current : cloud.getProducedSnowFlakes()) {
      if (current.getDescription().equals(removedSf.getDescription())) {
        present = true;
      }
    }
    assertThat(present).isFalse();
    for (SnowFlake current : cloud.getProducedSnowFlakes()) {
      session.delete(current);
    }
    session.delete(session.load(SnowFlake.class, removedSf.getId()));
    cloud.getProducedSnowFlakes().clear();
    session.flush();
    session.clear();

    cloud = (Cloud) session.get(Cloud.class, cloud.getId());
    assertThat(cloud.getProducedSnowFlakes()).isNotNull();
    assertThat(cloud.getProducedSnowFlakes()).hasSize(0);
    session.delete(cloud);
    session.flush();
  }
}
