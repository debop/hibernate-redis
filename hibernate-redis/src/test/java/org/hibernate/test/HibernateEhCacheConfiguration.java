package org.hibernate.test;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.ehcache.EhCacheRegionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * org.hibernate.test.HibernateEhCacheConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 8. 29. 오후 2:21
 */
@Slf4j
@Configuration
public class HibernateEhCacheConfiguration extends HibernateRedisConfiguration {

    @Override
    public Properties hibernateProperties() {
        Properties props = super.hibernateProperties();

        // Secondary Cache
        props.put(Environment.CACHE_REGION_FACTORY, EhCacheRegionFactory.class.getName());
        props.put(Environment.CACHE_PROVIDER_CONFIG, "classpath:ehcache.xml");

        return props;
    }
}
