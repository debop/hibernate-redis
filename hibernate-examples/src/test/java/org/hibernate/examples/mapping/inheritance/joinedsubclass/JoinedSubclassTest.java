package org.hibernate.examples.mapping.inheritance.joinedsubclass;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.inheritance.joinedsubclass.JoinedSubclassTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 30. 오후 12:53
 */
@Slf4j
@Transactional
public class JoinedSubclassTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void joinedSubclas() throws Exception {
    Company company = new Company();
    company.setName("HealthConnect");

    Employee employee = new Employee();
    employee.setName("배성혁");
    employee.setRegidentNo("111111-11111111");
    employee.setEmpNo("21011");
    employee.setCompany(company);

    Customer customer = new Customer();
    customer.setName("customer1");
    customer.setRegidentNo("222222-22222222");
    customer.setContactEmployee(employee);

    em.persist(company);
    em.persist(employee);
    em.persist(customer);
    em.flush();

    em.clear();

    Customer customer1 = em.find(Customer.class, customer.getId());
    assertThat(customer1).isEqualTo(customer);
    assertThat(customer1.getContactEmployee()).isEqualTo(employee);
    assertThat(customer1.getContactEmployee().getCompany()).isEqualTo(company);

    Employee employee1 = em.find(Employee.class, employee.getId());
    assertThat(employee1).isEqualTo(employee);
    assertThat(employee1.getCompany()).isEqualTo(company);
  }
}
