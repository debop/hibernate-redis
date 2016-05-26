package org.hibernate.examples.mapping.associations.unidirectional;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.associations.unidirectional.JpaCollectionUnidirectionalTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 4:26
 */
@Slf4j
@Transactional
public class JpaCollectionUnidirectionalTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void unidirectionalCollection() throws Exception {

    SnowFlake sf = new SnowFlake();
    sf.setDescription("Snowflake 1");
    em.persist(sf);

    SnowFlake sf2 = new SnowFlake();
    sf2.setDescription("Snowflake 2");
    em.persist(sf2);

    Cloud cloud = new Cloud();
    cloud.setLength(23.0);
    cloud.getProducedSnowFlakes().add(sf);
    cloud.getProducedSnowFlakes().add(sf2);
    em.persist(cloud);
    em.flush();

    em.clear();

    cloud = (Cloud) em.find(Cloud.class, cloud.getId());
    assertThat(cloud.getProducedSnowFlakes()).isNotNull();
    assertThat(cloud.getProducedSnowFlakes()).hasSize(2);

    final SnowFlake removedSf = cloud.getProducedSnowFlakes().iterator().next();
    SnowFlake sf3 = new SnowFlake();
    sf3.setDescription("Snowflake 3");
    em.persist(sf3);

    cloud.getProducedSnowFlakes().remove(removedSf);
    cloud.getProducedSnowFlakes().add(sf3);

    em.flush();
    em.clear();

    cloud = (Cloud) em.find(Cloud.class, cloud.getId());
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
      em.remove(current);
    }
    em.remove(em.find(SnowFlake.class, removedSf.getId()));
    cloud.getProducedSnowFlakes().clear();
    em.flush();
    em.clear();

    cloud = (Cloud) em.find(Cloud.class, cloud.getId());
    assertThat(cloud.getProducedSnowFlakes()).isNotNull();
    assertThat(cloud.getProducedSnowFlakes()).hasSize(0);
    em.remove(cloud);
    em.flush();
  }
}
