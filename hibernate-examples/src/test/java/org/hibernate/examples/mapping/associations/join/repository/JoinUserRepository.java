package org.hibernate.examples.mapping.associations.join.repository;

import org.hibernate.examples.mapping.associations.join.JoinUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * org.hibernate.examples.mapping.associations.join.repository.JoinUserRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 11:18
 */
public interface JoinUserRepository
    extends JpaRepository<JoinUser, Long>, QueryDslPredicateExecutor<JoinUser> {
}
