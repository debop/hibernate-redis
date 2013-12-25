package org.hibernate.examples.jpa.config;

import org.hibernate.examples.config.JpaHSqlConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * org.hibernate.examples.jpa.config.JpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 10:37
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = { JpaAccountRepository.class })
@EnableTransactionManagement
public class JpaConfiguration extends JpaHSqlConfiguration {

    @Override
    public String[] getMappedPackageNames() {
        return new String[] {
                JpaAccount.class.getPackage().getName()
        };
    }
}
