package org.hibernate.examples.hibernate.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.examples.AbstractHibernateTest;
import org.hibernate.examples.mapping.Employee;
import org.hibernate.examples.mapping.simple.SimpleEntity;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.hibernate.repository.HibernateDaoTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 9:43
 */
@Slf4j
@Transactional
public class HibernateDaoTest extends AbstractHibernateTest {

  @Test
  @Transactional(readOnly = true)
  public void createHibernateRepository() {
    assertThat(dao).isNotNull();

    List<SimpleEntity> users = dao.findAll(SimpleEntity.class);
    assertThat(users.size()).isEqualTo(0);
  }

  @Test
  @Transactional(readOnly = true)
  public void createEmployeeHiberateRepository() {
    List<Employee> categories = dao.findAll(Employee.class);
    assertThat(categories.size()).isEqualTo(0);
  }

  @Test
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public void loadSessionFactory() {
    Session session = sessionFactory.openSession();
    assertThat(session).isNotNull();
    List<SimpleEntity> events = (List<SimpleEntity>) session.createCriteria(SimpleEntity.class).list();
  }

  @Test
  @Transactional(readOnly = true)
  public void findAllTest() throws Exception {
    List<Employee> categories = dao.findAll(Employee.class);
    assertThat(categories).isNotNull();
    assertThat(categories.size()).isEqualTo(0);
  }
}
