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

package org.hibernate.cache.redis.jpa;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.hibernate52.CustomRedisRegionFactory;
import org.hibernate.cache.redis.jpa.models.Account;
import org.hibernate.cache.redis.jpa.repository.EventRepository;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
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
    props.put(Environment.CACHE_REGION_FACTORY, CustomRedisRegionFactory.class.getName());
    props.put(Environment.CACHE_REGION_PREFIX, "hibernate");
    props.put(Environment.CACHE_PROVIDER_CONFIG, "conf/hibernate-redis.properties");

    props.setProperty(Environment.GENERATE_STATISTICS, "true");
    props.setProperty(Environment.USE_STRUCTURED_CACHE, "true");

    // NOTE: Don't use TRANSACTION_COORDINATOR_STRATEGY in JPA
//    props.setProperty(Environment.TRANSACTION_COORDINATOR_STRATEGY, JtaTransactionCoordinatorBuilderImpl.class.getName());

    return props;
  }

  @Bean(destroyMethod = "close")
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
