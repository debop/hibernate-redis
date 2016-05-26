package org.hibernate.examples.jpa.config;

import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * PostgreSql DB를 사용하는 Spring 용 Hibernate 환경설정 정보
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:48
 */
@Configuration
@EnableTransactionManagement
public abstract class AbstractPostgreSqlJpaConfiguration extends AbstractJpaConfiguration {

  public static final String DRIVER_CLASS_POSTGRESQL = "org.postgresql.Driver";
  public static final String DIALECT_POSTGRESQL = "org.hibernate.dialect.PostgreSQL82Dialect";

  @Bean
  @Override
  public DataSource dataSource() {
    return buildDataSource(DRIVER_CLASS_POSTGRESQL,
        "jdbc:postgresql://localhost/" + getDatabaseName() + "?Set=UTF8",
        "root",
        "root");
  }

  @Override
  public Properties jpaProperties() {
    Properties props = super.jpaProperties();
    props.put(Environment.DIALECT, DIALECT_POSTGRESQL);
    return props;
  }
}
