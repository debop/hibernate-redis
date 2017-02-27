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

package org.hibernate.examples.hibernate.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.hibernate.config.HibernateConfigTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 9:43
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HibernateConfig.class})
public class HibernateConfigTest {

  @Autowired
  SessionFactory sessionFactory;

  @Test
  @Transactional(readOnly = true)
  public void configuration() throws Exception {
    assertThat(sessionFactory).isNotNull();
    assertThat(sessionFactory.getCurrentSession()).isNotNull();
  }

  @Test
  @Transactional
  public void crud() throws Exception {
    Session session = sessionFactory.getCurrentSession();

    Account account = new Account();
    account.setName("John Smith");
    account.setCashBalance(500.0);
    session.save(account);
    session.flush();
    session.clear();

    Query query = session.createQuery("from Account a where a.id=:id").setParameter("id", account.getId());//.setCacheable(true);
    Account a = (Account) query.uniqueResult();
    assertThat(a).isNotNull();
    assertThat(a.isPersisted()).isTrue();
    a.setName("foo");
    session.saveOrUpdate(a);
    session.flush();
  }
}
