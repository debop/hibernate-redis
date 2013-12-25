package org.hibernate.examples.hibernate.config;

import org.hibernate.examples.config.HibernateHSqlConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * org.hibernate.examples.hibernate.config.HibernateConfig
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 9:44
 */
@Configuration
@EnableTransactionManagement
public class HibernateConfig extends HibernateHSqlConfiguration {

    @Override
    public String[] getMappedPackageNames() {
        return new String[] { Account.class.getPackage().getName() };
    }
}
