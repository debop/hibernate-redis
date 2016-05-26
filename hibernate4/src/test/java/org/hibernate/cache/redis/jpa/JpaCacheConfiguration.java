package org.hibernate.cache.redis.jpa;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.hibernate4.SingletonRedisRegionFactory;
import org.hibernate.cache.redis.jpa.models.Account;
import org.hibernate.cache.redis.jpa.repository.EventRepository;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = {EventRepository.class})
public class JpaCacheConfiguration {

  public String getDatabaseName() {
    return "hibernate";
  }

  /**
   * 매핑할 엔티티 클래스가 정의된 package name 의 배열
   */
  public String[] getMappedPackageNames() {
    return new String[]{
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
    props.put(Environment.CACHE_REGION_PREFIX, "hibernate4:");
    props.put(Environment.CACHE_PROVIDER_CONFIG, "conf/hibernate-redis.properties");

    props.setProperty(Environment.GENERATE_STATISTICS, "true");
    props.setProperty(Environment.USE_STRUCTURED_CACHE, "true");
    props.setProperty(Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName());

    return props;
  }

  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();

    config.setDriverClassName("org.h2.Driver");
    config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=TRUE;");
    config.setUsername("sa");
    config.setPassword("");

    config.setInitializationFailFast(true);
    config.setConnectionTestQuery("SELECT 1");

    return new HikariDataSource(config);
  }

  @Bean
  public EntityManagerFactory entityManagerFactory() throws IOException {
    log.info("Create EntityManagerFactory Bean...");

    LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

    String[] packagenames = getMappedPackageNames();
    if (packagenames != null && packagenames.length > 0) {
      log.debug("Scan JPA entities... packages=[{}]", arrayToCommaDelimitedString(packagenames));
      factoryBean.setPackagesToScan(packagenames);
    }

    factoryBean.setJpaProperties(jpaProperties());
    factoryBean.setDataSource(dataSource());

    HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setGenerateDdl(true);
    factoryBean.setJpaVendorAdapter(adapter);

    factoryBean.afterPropertiesSet();
    log.info("Created EntityManagerFactory Bean");

    return factoryBean.getObject();
  }

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
