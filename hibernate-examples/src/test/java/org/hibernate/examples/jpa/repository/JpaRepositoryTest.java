package org.hibernate.examples.jpa.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.hibernate.examples.mapping.Employee;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.jpa.repository.JpaRepositoryTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 10:58
 */
@Slf4j
@Transactional
public class JpaRepositoryTest extends AbstractJpaTest {

  @Autowired
  EmployeeRepository empRepository;
  @PersistenceContext
  EntityManager em;

  @Test
  public void injectEmployeeRepository() throws Exception {
    assertThat(empRepository).isNotNull();
    List<Employee> employees = empRepository.findAll();
    assertThat(employees).isNotNull();
  }

  @Test
  public void employeeFindByEmpNo() {
    Employee emp = new Employee();
    emp.setName("Sunghyouk Bae");
    emp.setEmpNo("21011");
    emp = empRepository.saveAndFlush(emp);
    em.clear();

    Employee loaded = empRepository.findByEmpNo(emp.getEmpNo());
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualTo(emp);
    assertThat(loaded.getUpdatedTimestamp()).isNotNull();
    log.debug("Employee=[{}]", loaded);
  }
}
