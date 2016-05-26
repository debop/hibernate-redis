package org.hibernate.examples.mapping;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.examples.AbstractHibernateTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * org.hibernate.examples.mapping.HibernateMappingTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:42
 */
@Slf4j
public class HibernateMappingTest extends AbstractHibernateTest {

  @Autowired
  SessionFactory sessionFactory;

  @Test
  public void mappingTest() throws Exception {
    assertThat(sessionFactory).isNotNull();
  }

}
