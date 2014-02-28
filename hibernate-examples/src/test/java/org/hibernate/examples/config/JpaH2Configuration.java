package org.hibernate.examples.config;

import org.hibernate.cache.redis.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.examples.jpa.config.AbstractH2JpaConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * org.hibernate.examples.JpaHSqConfig
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:36
 */
@Configuration
@EnableJpaRepositories(basePackages = "org.hibernate.examples")
@EnableTransactionManagement
public class JpaH2Configuration extends AbstractH2JpaConfiguration {


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
