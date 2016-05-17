package org.hibernate.examples.jpa.config;

import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * MySQL DB를 사용하는 Spring 용 Hibernate 환경설정 정보
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:36
 */
@Configuration
@EnableTransactionManagement
public abstract class AbstractMySqlJpaConfiguration extends AbstractJpaConfiguration {

  public static final String DRIVER_CLASS_MYSQL = "com.mysql.jdbc.Driver";
  public static final String DIALECT_MYSQL = "org.hibernate.dialect.MySQL5InnoDBDialect";

  @Bean
  @Override
  public DataSource dataSource() {
    return buildDataSource(DRIVER_CLASS_MYSQL,
        "jdbc:mysql://localhost/" + getDatabaseName(),
        "root",
        "root");
  }

  @Override
  public Properties jpaProperties() {
    Properties props = super.jpaProperties();
    props.put(Environment.DIALECT, DIALECT_MYSQL);
    return props;
  }

}
