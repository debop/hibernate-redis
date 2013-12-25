package org.hibernate.examples.jpa.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * org.hibernate.examples.jpa.config.JpaAccountRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 10:34
 */
@Transactional
public interface JpaAccountRepository extends JpaRepository<JpaAccount, Long> {

    @Query(value = "select a from JpaAccount a where a.name=:name")
    JpaAccount findByName(@Param("name") final String name);
}
