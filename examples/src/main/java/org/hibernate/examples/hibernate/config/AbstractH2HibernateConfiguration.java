package org.hibernate.examples.hibernate.config;

import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * AbstractH2HibernateConfiguration
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
public abstract class AbstractH2HibernateConfiguration extends AbstractHibernateConfiguration {

  public static final String DRIVER_CLASS_H2 = "org.h2.Driver";
  public static final String DIALECT_H2 = "org.hibernate.dialect.H2Dialect";

  @Bean
  @Override
  public DataSource dataSource() {
    return buildDataSource(DRIVER_CLASS_H2,
        "jdbc:h2:mem:" + getDatabaseName() + ";DB_CLOSE_DELAY=-1;MVCC=TRUE;",
        "sa",
        "");
  }

  @Override
  public Properties hibernateProperties() {
    Properties props = super.hibernateProperties();
    props.put(Environment.DIALECT, DIALECT_H2);
    return props;
  }
}
