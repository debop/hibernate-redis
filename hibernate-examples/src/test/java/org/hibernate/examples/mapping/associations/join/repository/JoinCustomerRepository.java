package org.hibernate.examples.mapping.associations.join.repository;

import org.hibernate.examples.mapping.associations.join.JoinCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * org.hibernate.examples.mapping.associations.join.repository.JoinCustomerRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 11:18
 */
public interface JoinCustomerRepository extends JpaRepository<JoinCustomer, Long>, QueryDslPredicateExecutor<JoinCustomer> {

  @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  JoinCustomer findByName(String name);

  @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  List<JoinCustomer> findByNameLike(String name);

  @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  JoinCustomer findByEmail(String email);
}
