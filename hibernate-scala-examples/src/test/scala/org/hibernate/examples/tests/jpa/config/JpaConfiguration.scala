package org.hibernate.examples.tests.jpa.config

import org.hibernate.examples.tests.config.JpaH2Configuration
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * org.hibernate.examples.tests.jpa.config.JpaConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:29
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = Array(classOf[JpaAccountRepository]))
@EnableTransactionManagement
class JpaConfiguration extends JpaH2Configuration {

    override def getMappedPackageNames: Array[String] =
        Array(classOf[JpaAccount].getPackage.getName)
}
