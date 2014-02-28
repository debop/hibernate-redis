package org.hibernate.examples.config;

import org.hibernate.cache.redis.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.examples.jpa.config.AbstractMySqlJpaConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * org.hibernate.examples.config.JpaMySqlConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 6. 오후 10:17
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.hibernate.examples")
@EnableTransactionManagement
public class JpaMySqlConfiguration extends AbstractMySqlJpaConfiguration {

    @Override
    public String getDatabaseName() {
        return "hibernate";
    }

    @Override
    public String[] getMappedPackageNames() {
        return new String[]{
                "org.hibernate.examples.mapping"
        };
    }

    @Override
    public Properties jpaProperties() {
        Properties props = super.jpaProperties();

        props.put(Environment.HBM2DDL_AUTO, "create"); // create | spawn | spawn-drop | update | validate | none

        // hibernate second level cache
        props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        props.put(Environment.USE_QUERY_CACHE, true);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        props.put(Environment.CACHE_REGION_PREFIX, "");
        props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");


        return props;
    }
}
