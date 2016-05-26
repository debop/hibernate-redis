package org.hibernate.examples.jpa.repository;

import org.hibernate.examples.mapping.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;

/**
 * org.hibernate.examples.jpa.repository.EmployeeRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 10:55
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long>, QueryDslPredicateExecutor<Employee> {

  @Query("select e from Employee e where e.empNo=:empNo")
  @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  Employee findByEmpNo(@Param("empNo") final String empNo);

  @Query("select e from Employee e where e.empNo=:empNo and e.email=:email")
  @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  Employee findByEmpNoAndEmail(@Param("empNo") final String empNo,
                               @Param("email") final String email);
}
