package org.hibernate.examples.tests.config

import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.context.annotation.Configuration
import org.hibernate.examples.hibernate.config.AbstractH2HibernateConfiguration

/**
 * org.hibernate.examples.tests.config.JpaHSqlConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:13
 */
@Configuration
@EnableTransactionManagement
class HibernateH2Configuration extends AbstractH2HibernateConfiguration {

    override def getMappedPackageNames: Array[String] =
        Array[String]("org.hibernate.examples.mapping")
}
