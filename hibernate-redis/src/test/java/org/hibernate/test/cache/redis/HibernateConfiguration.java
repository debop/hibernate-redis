package org.hibernate.test.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * org.hibernate.test.cache.redis.HibernateConfiguration
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오후 12:30
 */
@Configuration
@Slf4j
public class HibernateConfiguration {

    public DataSource getDataSource(String driverClass, String url, String username, String passwd) {
        if (log.isDebugEnabled())
            log.debug("build Tomcat pool DataSource... driverClass=[{}], url=[{}], username=[{}], passwd=[{}]",
                      driverClass, url, username, passwd);

        PoolProperties p = new PoolProperties();
        p.setUrl(url);
        p.setDriverClassName(driverClass);
        p.setUsername(username);
        p.setPassword(passwd);

        p.setJmxEnabled(true);
        p.setTestWhileIdle(true);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(200);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);

        DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(p);
        return ds;
    }

    @Bean
    public Properties hibernateProperties() {

        Properties props = new Properties();

        props.put(Environment.FORMAT_SQL, "true");
        props.put(Environment.HBM2DDL_AUTO, "create"); // create | spawn | spawn-drop | update | validate
        props.put(Environment.SHOW_SQL, "true");
        props.put(Environment.RELEASE_CONNECTIONS, ConnectionReleaseMode.ON_CLOSE);
        props.put(Environment.AUTOCOMMIT, "true");
        props.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        props.put(Environment.STATEMENT_BATCH_SIZE, "50");

        props.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");

        return props;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        return getDataSource("org.h2.Driver",
                             "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=TRUE",
                             "sa",
                             "");
    }

    @Bean
    org.hibernate.cfg.Configuration hibernateConfiguration() {

        org.hibernate.cfg.Configuration cfg = new org.hibernate.cfg.Configuration();
        cfg.addProperties(hibernateProperties());
        cfg.getProperties().put(Environment.DATASOURCE, dataSource());

        addHibernateConfig(cfg);

        return cfg;
    }

    /**
     * 재정의 해서 환경설정을 추가해 주세요.
     *
     * @param cfg
     */
    public void addHibernateConfig(org.hibernate.cfg.Configuration cfg) {
        cfg.addAnnotatedClass(Item.class);
    }

    @Bean
    public SessionFactory sessionFactory() {

        if (log.isInfoEnabled())
            log.info("SessionFactory를 생성합니다...");

        try {
            org.hibernate.cfg.Configuration cfg = hibernateConfiguration();

            ServiceRegistry serviceRegistry =
                    new ServiceRegistryBuilder()
                            .applySettings(cfg.getProperties())
                            .buildServiceRegistry();

            if (log.isInfoEnabled())
                log.info("SessionFactory를 생성했습니다!!!");

            return cfg.buildSessionFactory(serviceRegistry);

        } catch (Exception e) {
            throw new RuntimeException("SessionFactory 빌드에 실패했습니다.", e);
        }
    }
}
