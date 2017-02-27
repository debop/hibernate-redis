/*
 * Copyright (c) 2017. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.stresser.persistence.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQL5InnoDBDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
@Configuration
@EnableTransactionManagement
public class PersistenceContext {

  @Autowired
  private DatabaseConfig databaseConfig;

  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    if (databaseConfig.getMinimumIdle() > 0) {
      config.setMinimumIdle(databaseConfig.getMinimumIdle());
    }
    config.setMaximumPoolSize(databaseConfig.getMaximumPoolSize());
    config.setIdleTimeout(TimeUnit.SECONDS.toMillis(databaseConfig.getIdleTimeout()));
    config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(databaseConfig.getConnectionTimeout()));
    config.setDriverClassName(databaseConfig.getDriver());
    config.setJdbcUrl(databaseConfig.getUrl());
    config.addDataSourceProperty("user", databaseConfig.getUser());
    config.addDataSourceProperty("password", databaseConfig.getPassword());
    config.addDataSourceProperty("cachePrepStmts", true);
    config.addDataSourceProperty("prepStmtCacheSize", 250);
    config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
    config.addDataSourceProperty("useServerPrepStmts", true);
    config.addDataSourceProperty("rewriteBatchedStatements", true);

    return new HikariDataSource(config); // fastPathPool ftw
  }

  @Bean
  @DependsOn(value = "redisClientProvider")
  public SessionFactory sessionFactory() {
    LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource());
    builder.scanPackages("org.hibernate.stresser.persistence").addProperties(getHibernateProperties()); // .setNamingStrategy(new ImprovedNamingStrategy())
    return builder.buildSessionFactory();
  }

  private Properties getHibernateProperties() {
    Properties properties = new Properties();

    // Configures the used database dialect. This allows Hibernate to create SQL that is optimized for the used database.
    properties.put(Environment.DIALECT, MySQL5InnoDBDialect.class.getName());

    // Specifies the action that is invoked to the database when the Hibernate SessionFactory is created or closed.
    properties.put(Environment.HBM2DDL_AUTO, "create");

    // Hibernate statistics for JMX.
    properties.put(Environment.GENERATE_STATISTICS, true);

    properties.put(Environment.USE_SECOND_LEVEL_CACHE, true);
    properties.put(Environment.USE_QUERY_CACHE, true);
    properties.put(Environment.USE_MINIMAL_PUTS, true);
    properties.put(Environment.STATEMENT_BATCH_SIZE, 200);

    // Configure second level cache.
    properties.put(Environment.CACHE_REGION_FACTORY, RedisRegionFactory.class.getName());
    properties.put(Environment.USE_STRUCTURED_CACHE, false);
    properties.put(Environment.CACHE_REGION_PREFIX, "hibernate");
    properties.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");

    // If the value of this property is true, Hibernate writes all SQL statements to the console.
    properties.put(Environment.SHOW_SQL, false);

    // If the value of this property is true, Hibernate will use pretty-print when it writes SQL to the console.
    properties.put(Environment.FORMAT_SQL, false);

    return properties;
  }

  @Bean
  public HibernateTransactionManager transactionManager() {
    return new HibernateTransactionManager(sessionFactory());
  }

  @Bean
  public HibernateExceptionTranslator hibernateExceptionTranslator() {
    return new HibernateExceptionTranslator();
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }
}
