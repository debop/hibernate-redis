package org.hibernate.examples.mapping.property.enumerated;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.property.enumerated.EnumeratedEntityTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 2:01
 */
@Slf4j
@Transactional
public class EnumeratedEntityTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  static EnumeratedEntity entity = new EnumeratedEntity();

  @Test
  public void enumerated() throws Exception {
    entity.setIntValue(OrdinalEnum.Second);
    entity.setStringValue(StringEnum.Integer);

    em.persist(entity);
    em.flush();
    em.clear();

    log.debug("load from database");
    EnumeratedEntity loaded = em.find(EnumeratedEntity.class, entity.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualTo(entity);
    assertThat(loaded.getIntValue()).isEqualTo(OrdinalEnum.Second);
    assertThat(loaded.getStringValue()).isEqualTo(StringEnum.Integer);

    em.clear();

    log.debug("load from cache");
    loaded = em.find(EnumeratedEntity.class, entity.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualTo(entity);
    assertThat(loaded.getIntValue()).isEqualTo(OrdinalEnum.Second);
    assertThat(loaded.getStringValue()).isEqualTo(StringEnum.Integer);
  }
}
