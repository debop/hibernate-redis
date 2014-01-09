package org.hibernate.examples.tests.jpa.config

import org.springframework.data.jpa.repository.{Query, JpaRepository}
import org.springframework.data.repository.query.Param

/**
 * org.hibernate.examples.tests.jpa.config.JpaAccountRepository 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:28
 */
trait JpaAccountRepository extends JpaRepository[JpaAccount, java.lang.Long] {

    @Query(value = "select a from JpaAccount a where a.name = :name")
    def findByName(@Param("name") name: String): JpaAccount

}
