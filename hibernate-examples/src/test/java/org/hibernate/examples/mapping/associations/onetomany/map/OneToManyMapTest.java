package org.hibernate.examples.mapping.associations.onetomany.map;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.associations.onetomany.map.OneToManyMapTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 1:25
 */
@Slf4j
@Transactional
public class OneToManyMapTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void mapTest() throws Exception {

    OneToManyCar car = new OneToManyCar();

    OneToManyCarOption option1 = new OneToManyCarOption("option1", 1);
    OneToManyCarOption option2 = new OneToManyCarOption("option2", 1);

    car.getCarOptions().put("option1", option1);
    car.getCarOptions().put("option2", option2);

    car.getOptions().put("stringOption1", "Value1");
    car.getOptions().put("stringOption2", "Value2");

    em.persist(car);
    em.flush();
    em.clear();

    OneToManyCar loaded = em.find(OneToManyCar.class, car.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getCarOptions()).hasSize(2);
    assertThat(loaded.getOptions()).hasSize(2);

  }
}
