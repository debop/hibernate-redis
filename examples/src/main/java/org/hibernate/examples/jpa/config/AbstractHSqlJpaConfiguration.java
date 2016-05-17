package org.hibernate.examples.jpa.config;

import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * HSQL DB를 사용하는 Spring 용 Hibernate 환경설정 정보
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:33
 */
@Configuration
@EnableTransactionManagement
public abstract class AbstractHSqlJpaConfiguration extends AbstractJpaConfiguration {

  public static final String DRIVER_CLASS_HSQL = "org.hsqldb.jdbcDriver";
  public static final String DIALECT_HSQL = "org.hibernate.dialect.HSQLDialect";

  @Override
  public DataSource dataSource() {
    return buildDataSource(DRIVER_CLASS_HSQL,
        "jdbc:hsqldb:mem:" + getDatabaseName() + ";MVCC=TRUE;",
        "sa",
        "");
  }

  @Override
  public Properties jpaProperties() {
    Properties props = super.jpaProperties();
    props.put(Environment.DIALECT, DIALECT_HSQL);
    return props;
  }
}
