package org.hibernate.examples.hibernate.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.examples.hibernate.interceptor.PersistentObjectInterceptor;
import org.hibernate.examples.hibernate.repository.HibernateDao;
import org.hibernate.examples.hibernate.repository.HibernateDaoImpl;
import org.hibernate.examples.utils.DataSources;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;

/**
 * Spring 용 Hibernate 환경 설정 Class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 9:54
 */
@Slf4j
public abstract class AbstractHibernateConfiguration {

  public String getDatabaseName() {
    return "hibernate";
  }

  /**
   * Mapping Enitty 들이 정의된 Package 명의 배열을 반환합니다.
   *
   * @return pacakge name 의 배열
   */
  abstract public String[] getMappedPackageNames();

  /**
   * HBM 을 이용하여 Database Schema를 생성할 때, 쿼리문을 DB에 맞게 변환할 때 사용하는 규칙
   *
   * @return NamingStrategy instance
   */
  public NamingStrategy getNamingStrategy() {
    return null; // ImprovedNamingStrategy.INSTANCE;
  }

  /**
   * Hibernate 환경 설정 정보
   *
   * @return Hibernate 환경 설정 정보를 담은 {@link Properties}
   */
  public Properties hibernateProperties() {
    Properties props = new Properties();

    props.put(Environment.FORMAT_SQL, "true");
    props.put(Environment.HBM2DDL_AUTO, "create"); // create | spawn | spawn-drop | update | validate | none

    props.put(Environment.SHOW_SQL, "true");
    props.put(Environment.RELEASE_CONNECTIONS, ConnectionReleaseMode.ON_CLOSE);
    props.put(Environment.AUTOCOMMIT, "true");
    props.put(Environment.STATEMENT_BATCH_SIZE, "100");

    return props;
  }

  /**
   * Hibernate 가 사용할 Database 정보를 담은 {@link DataSource}
   *
   * @param driverClass JDBC Driver class
   * @param url         JDBC Database address (url)
   * @param username    user name
   * @param password    user password
   * @return {@link DataSource} 인스턴스
   */
  protected DataSource buildDataSource(String driverClass, String url, String username, String password) {
    return DataSources.getDataSource(driverClass, url, username, password);
  }

  /**
   * Embeded database 용 DataSource를 빌드합니다.
   * 테스트 시에 사용할 메모리 DB를 생성합니다.
   *
   * @return {@link DataSource} 인스턴스
   */
  protected DataSource buildEmbeddedDataSource() {
    return DataSources.getEmbeddedHSqlDataSource();
  }

  /**
   * {@link DataSource} Bean
   *
   * @return {@link DataSource} 인스턴스
   */
  @Bean
  public DataSource dataSource() {
    return buildDataSource("org.hsqldb.jdbcDriver",
        "jdbc:hsqldb:mem:" + getDatabaseName() + ";MVCC=TRUE;",
        "sa",
        "");
  }

  /**
   * SessionFactory 추가 작업을 하고자할 때 재정의해서 사용합니다.
   *
   * @param factoryBean {@link LocalSessionFactoryBean} instance
   */
  protected void setupSessionFactory(LocalSessionFactoryBean factoryBean) {
    // 추가 작업 시 override 해서 사용합니다.
  }

  /**
   * Hibernate {@link SessionFactory} 를 빌드합니다.
   *
   * @return {@link SessionFactory} instance.
   */
  @Bean
  public SessionFactory sessionFactory() throws IOException {
    log.info("SessionFactory Bean을 생성합니다...");

    LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();

    String[] packagenames = getMappedPackageNames();
    if (packagenames != null && packagenames.length > 0) {
      log.debug("hibernate용 entity를 scan합니다. packages=[{}]", arrayToCommaDelimitedString(packagenames));
      factoryBean.setPackagesToScan(packagenames);
    }

    // Naming strategy (for Hibernate 4)
//    NamingStrategy ns = getNamingStrategy();
//    if (ns != null)
//      factoryBean.setNamingStrategy(ns);

    // Hibernate properties
    factoryBean.setHibernateProperties(hibernateProperties());
    factoryBean.setDataSource(dataSource());

    Interceptor interceptor = hibernateInterceptor();
    if (interceptor != null)
      factoryBean.setEntityInterceptor(hibernateInterceptor());

    setupSessionFactory(factoryBean);

    factoryBean.afterPropertiesSet();
    log.info("SessionFactory Bean을 생성했습니다!!!");

    return factoryBean.getObject();
  }

  /**
   * {@link HibernateTransactionManager} Bean 을 등록합니다.
   *
   * @return {@link HibernateTransactionManager} instance.
   */
  @Bean
  public HibernateTransactionManager transactionManager() throws IOException {
    return new HibernateTransactionManager(sessionFactory());
  }

  /**
   * Hibernate Interceptor
   *
   * @return hibernate interceptor
   */
  @Bean
  public Interceptor hibernateInterceptor() {
    return new PersistentObjectInterceptor();
  }

  @Bean
  public HibernateDao hibernateDao() {
    return new HibernateDaoImpl();
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
