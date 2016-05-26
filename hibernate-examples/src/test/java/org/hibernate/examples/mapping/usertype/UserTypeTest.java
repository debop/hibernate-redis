package org.hibernate.examples.mapping.usertype;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.hibernate.examples.model.DateTimeRange;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.usertype.UserTypeTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 9:15
 */
@Slf4j
@Transactional
public class UserTypeTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void jodaDateTimeUserTypeTest() {
    JodaDateTimeEntity entity = new JodaDateTimeEntity();

    entity.setStart(DateTime.now().withTimeAtStartOfDay());
    entity.setEnd(entity.getStart().plusDays(1));

    entity.setRange1(new DateTimeRange(entity.getStart(), entity.getEnd()));
    entity.setRange2(new DateTimeRange(entity.getStart().plusDays(1), entity.getEnd().plusDays(1)));

    em.persist(entity);
    em.flush();
    em.clear();

    JodaDateTimeEntity loaded = em.find(JodaDateTimeEntity.class, entity.getId());

    assertThat(loaded).isEqualTo(entity);
    assertThat(loaded.getStart()).isEqualTo(entity.getStart());
    assertThat(loaded.getEnd()).isEqualTo(entity.getEnd());
    assertThat(loaded.getRange1()).isEqualTo(entity.getRange1());
    assertThat(loaded.getRange2()).isEqualTo(entity.getRange2());
  }

  @Test
  public void jodaDateTimeTZUserTypeTest() {
    JodaDateTimeTZEntity entity = new JodaDateTimeTZEntity();

    entity.setStartTZ(DateTime.now().withTimeAtStartOfDay());
    entity.setEndTZ(entity.getStartTZ().plusDays(1));

    em.persist(entity);
    em.flush();
    em.clear();

    JodaDateTimeTZEntity loaded = em.find(JodaDateTimeTZEntity.class, entity.getId());

    assertThat(loaded).isEqualTo(entity);
    assertThat(loaded.getStartTZ()).isEqualTo(entity.getStartTZ());
    assertThat(loaded.getEndTZ()).isEqualTo(entity.getEndTZ());
  }
}
