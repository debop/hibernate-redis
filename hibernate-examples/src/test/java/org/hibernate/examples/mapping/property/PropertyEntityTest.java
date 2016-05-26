package org.hibernate.examples.mapping.property;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.property.PropertyEntityTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 7. 오전 11:14
 */
@Slf4j
@Transactional
public class PropertyEntityTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void blob() throws Exception {
    PropertyEntity pe = new PropertyEntity();
    pe.setName("name");
    pe.setData("Long Long Data...");

    em.persist(pe);
    em.flush();
    em.clear();

    PropertyEntity loaded = em.find(PropertyEntity.class, pe.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualTo(pe);
  }
}
