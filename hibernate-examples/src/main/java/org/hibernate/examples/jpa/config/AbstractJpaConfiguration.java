package org.hibernate.examples.jpa.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.examples.utils.DataSources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
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
 * org.hibernate.examples.jpa.config.AbstractJpaConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:13
 */
@Configuration
@EnableTransactionManagement
@Slf4j
public abstract class AbstractJpaConfiguration {

  /**
   * JPA EntityManager가 사용할 Database 명
   */
  public String getDatabaseName() {
    return "hibernate";
  }

  /**
   * 매핑할 엔티티 클래스가 정의된 package name 의 배열
   */
  abstract public String[] getMappedPackageNames();

  /**
   * Database 테이블/컬럼 명에 대한 명명 규칙
   */
  public NamingStrategy getNamingStrategy() {
    return null; // return DefaultNamingStrategy.INSTANCE;
  }

  /**
   * JPA 환경 설정 정보
   *
   * @return 설정 정보
   */
  public Properties jpaProperties() {
    Properties props = new Properties();

    props.put(Environment.FORMAT_SQL, "true");
    props.put(Environment.HBM2DDL_AUTO, "create"); // create | spawn | spawn-drop | update | validate | none

    props.put(Environment.SHOW_SQL, "true");
    props.put(Environment.RELEASE_CONNECTIONS, ConnectionReleaseMode.ON_CLOSE);
    props.put(Environment.AUTOCOMMIT, "true");
    props.put(Environment.STATEMENT_BATCH_SIZE, "100");

    //        props.put(Environment.CACHE_REGION_PREFIX, "hibernate:");

    // NOTE: Naming Strategy (JPA 에서는 HibernatePersistence 를 사용해야 합니다)
    // props.put(HibernatePersistence.NAMING_STRATEGY, getNamingStrategy());

    return props;
  }

  /**
   * JPA 가 사용할 Database에 대한 {@link DataSource} 객체를 빌드합니다.
   *
   * @param driverClass driver class
   * @param url         database jdbc url
   * @param username    database user name
   * @param password    database user password
   * @return {@link DataSource} instance.
   */
  protected DataSource buildDataSource(String driverClass, String url, String username, String password) {
    return DataSources.getDataSource(driverClass, url, username, password);
  }

  protected DataSource buildEmbeddedDataSource() {
    return DataSources.getEmbeddedHSqlDataSource();
  }

  /**
   * JPA 용 {@link DataSource} Bean
   */
  @Bean
  public DataSource dataSource() {
    return buildDataSource("org.hsqldb.jdbcDriver",
        "jdbc:hsqldb:mem:" + getDatabaseName() + ";MVCC=TRUE;",
        "sa",
        "");
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
   * JPA {@link EntityManagerFactory} 를 빌드합니다.
   *
   * @return
   * @throws IOException
   */
  @Bean
  public EntityManagerFactory entityManagerFactory() throws IOException {
    AbstractJpaConfiguration.log.info("EntityManagerFactory Bean을 생성합니다...");

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
    AbstractJpaConfiguration.log.info("EntityManagerFactory Bean을 생성했습니다!!!");

    return factoryBean.getObject();
  }

  /**
   * JPA 용 Transaction Manager 를 생성합니다.
   *
   * @return {@link JpaTransactionManager} instance
   * @throws IOException
   */
  @Bean
  public PlatformTransactionManager transactionManager() throws IOException {
    return new JpaTransactionManager(entityManagerFactory());
  }

  /**
   * Hibernate 에외를 변환하는 {@link HibernateExceptionTranslator} 를 Spring 의 ApplicationContext에 등록합니다.
   */
  // NOTE: 이거 꼭 정의해야 합니다.
  @Bean
  public HibernateExceptionTranslator hibernateExceptionTranslator() {
    return new HibernateExceptionTranslator();
  }

  /**
   * 예외를 변환하는 Processor를 동록합니다.
   *
   * @return {@link PersistenceExceptionTranslationPostProcessor} instance.
   */
  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }
}
