package org.hibernate.examples.jpa.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.hibernate.examples.mapping.Employee;
import org.hibernate.examples.mapping.QEmployee;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.jpa.repository.QueryDslRepositoryTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 11:03
 */
@Slf4j
@Transactional
public class QueryDslRepositoryTest extends AbstractJpaTest {

  @Autowired
  EmployeeRepository employeeRepository;

  @PersistenceContext
  EntityManager em;

  @Test
  public void findAllTest() {
    Employee emp = new Employee();
    emp.setName("Sunghyouk Bae");
    emp.setEmpNo("21011");
    emp = employeeRepository.save(emp);

    QEmployee $ = QEmployee.employee;
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    Employee loaded = queryFactory.select($)
                                  .from($)
                                  .where($.empNo.eq("21011"))
                                  .fetchOne();

    assertThat(loaded).isNotNull();
    assertThat(loaded.getEmpNo()).isEqualTo(emp.getEmpNo());
    assertThat(loaded).isEqualTo(emp);
    assertThat(loaded.isPersisted()).isTrue();
  }
}
