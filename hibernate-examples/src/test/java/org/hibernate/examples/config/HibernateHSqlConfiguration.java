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

package org.hibernate.examples.config;

import org.hibernate.cache.redis.hibernate52.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.examples.hibernate.config.AbstractHSqlHibernateConfiguration;
import org.hibernate.examples.jpa.config.JpaAccount;
import org.hibernate.examples.mapping.Employee;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * org.hibernate.examples.config.HibernateHSqlConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:39
 */
@Configuration
@EnableTransactionManagement
public class HibernateHSqlConfiguration extends AbstractHSqlHibernateConfiguration {

  @Override
  public String[] getMappedPackageNames() {
    return new String[]{
        Employee.class.getPackage().getName(),
        JpaAccount.class.getPackage().getName()
    };
  }

  @Override
  public Properties hibernateProperties() {
    Properties props = super.hibernateProperties();

    props.put(Environment.HBM2DDL_AUTO, "create"); // create | spawn | spawn-drop | update | validate | none

    // hibernate second level cache
    props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
    props.put(Environment.USE_QUERY_CACHE, true);
    props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
    props.put(Environment.CACHE_REGION_PREFIX, "hibernate");
    props.put(Environment.CACHE_PROVIDER_CONFIG, "conf/hibernate-redis.properties");


    return props;
  }
}
