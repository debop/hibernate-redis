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

package org.hibernate.cache.redis.spring;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cache.redis.hibernate4.SingletonRedisRegionFactory;
import org.hibernate.cache.redis.jpa.models.Account;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Slf4j
@Configuration
public class HibernateRedisConfiguration {

  public String getDatabaseName() {
    return "hibernate";
  }

  public String[] getMappedPackageNames() {
    return new String[]{
        Account.class.getPackage().getName()
    };
  }

  public Properties hibernateProperties() {
    Properties props = new Properties();

    props.put(Environment.FORMAT_SQL, "true");
    props.put(Environment.HBM2DDL_AUTO, "create");
    props.put(Environment.SHOW_SQL, "true");

    props.put(Environment.POOL_SIZE, 30);

    // Secondary Cache
    props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
    props.put(Environment.USE_QUERY_CACHE, true);
    props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
    props.put(Environment.CACHE_REGION_PREFIX, "hibernate");
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
  public SessionFactory sessionFactory() throws IOException {
    LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
    factoryBean.setPackagesToScan(getMappedPackageNames());
    factoryBean.setDataSource(dataSource());
    factoryBean.setHibernateProperties(hibernateProperties());

    factoryBean.afterPropertiesSet();

    return factoryBean.getObject();
  }

  @Bean
  public PlatformTransactionManager transactionManager(SessionFactory sf) throws IOException {
    return new HibernateTransactionManager(sf);
  }

  @Bean
  public HibernateExceptionTranslator hibernateExceptionTranslator() {
    return new HibernateExceptionTranslator();
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslator() {
    return new PersistenceExceptionTranslationPostProcessor();
  }
}
