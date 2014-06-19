package org.hibernate.test.jpa;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.test.domain.Account;
import org.hibernate.test.jpa.repository.EventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;

/**
 * org.hibernate.test.jpa.JpaRedisConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 21. 오후 5:51
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = { EventRepository.class })
public class JpaRedisConfiguration {

    /**
     * JPA EntityManager가 사용할 Database 명
     */
    public String getDatabaseName() {
        return "hibernate";
    }

    /**
     * 매핑할 엔티티 클래스가 정의된 package name 의 배열
     */
    public String[] getMappedPackageNames() {
        return new String[] {
                Account.class.getPackage().getName()
        };
    }

    /**
     * JPA 환경 설정 정보
     *
     * @return 설정 정보
     */
    public Properties jpaProperties() {
        Properties props = new Properties();

        props.put(Environment.FORMAT_SQL, "true");
        props.put(Environment.HBM2DDL_AUTO, "create");
        props.put(Environment.SHOW_SQL, "true");

        props.put(Environment.POOL_SIZE, 30);

        // Secondary Cache
        props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        props.put(Environment.USE_QUERY_CACHE, true);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        props.put(Environment.CACHE_REGION_PREFIX, "");
        props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");

        return props;
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    /**
     * EntityManagerFactory 에 추가 설정 작업이 필요할 시에 재정의하여 설정을 추가합니다.
     *
     * @param factoryBean factory bean
     */
    protected void setupEntityManagerFactory(LocalContainerEntityManagerFactoryBean factoryBean) {
        // 추가 작업 시 override 해서 사용합니다.
    }

    /**
     * JPA {@link javax.persistence.EntityManagerFactory} 를 빌드합니다.
     *
     * @throws java.io.IOException
     */
    @Bean
    public EntityManagerFactory entityManagerFactory() throws IOException {
        log.info("EntityManagerFactory Bean을 생성합니다...");

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        String[] packagenames = getMappedPackageNames();
        if (packagenames != null && packagenames.length > 0) {
            log.debug("JPA용 entity를 scan합니다. packages=[{}]", arrayToCommaDelimitedString(packagenames));
            factoryBean.setPackagesToScan(packagenames);
        }

        factoryBean.setJpaProperties(jpaProperties());
        factoryBean.setDataSource(dataSource());

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        factoryBean.setJpaVendorAdapter(adapter);

        setupEntityManagerFactory(factoryBean);

        factoryBean.afterPropertiesSet();
        JpaRedisConfiguration.log.info("EntityManagerFactory Bean을 생성했습니다!!!");

        return factoryBean.getObject();
    }

    /**
     * JPA 용 Transaction Manager 를 생성합니다.
     *
     * @return {@link org.springframework.orm.jpa.JpaTransactionManager} instance
     * @throws IOException
     */
    @Bean
    public PlatformTransactionManager transactionManager() throws IOException {
        return new JpaTransactionManager(entityManagerFactory());
    }

    /**
     * Hibernate 에외를 변환하는 {@link org.springframework.orm.hibernate4.HibernateExceptionTranslator} 를 Spring 의 ApplicationContext에 등록합니다.
     */
    // NOTE: 이거 꼭 정의해야 합니다.
    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    /**
     * 예외를 변환하는 Processor를 동록합니다.
     *
     * @return {@link org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor} instance.
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
