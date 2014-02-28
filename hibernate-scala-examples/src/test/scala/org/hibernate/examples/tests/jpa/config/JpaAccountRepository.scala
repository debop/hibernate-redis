package org.hibernate.examples.tests.jpa.config

import org.springframework.data.jpa.repository.{Query, JpaRepository}
import org.springframework.data.repository.query.Param

/**
 * Spring Data JPA 용 Repository 입니다.
 * NOTE: QueryDSL for Scala 사용법을 몰라 QueryDslPredicateExecutor는 사용하지 못합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:28
 */
trait JpaAccountRepository extends JpaRepository[JpaAccount, java.lang.Long] {
    // with QueryDslPredicateExecutor[JpaAccount] {

    @Query(value = "select a from JpaAccount a where a.name = :name")
    def findByName(@Param("name") name: String): JpaAccount

}
