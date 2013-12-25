package org.hibernate.examples.hibernate.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.examples.hibernate.config.HibernateConfigTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 9:43
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateConfig.class })
public class HibernateConfigTest {

    @Autowired SessionFactory sessionFactory;

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

        Query query = session.createQuery("from Account a where a.id=:id").setLong("id", account.getId());
        Account a = (Account) query.uniqueResult();
        assertThat(a).isNotNull();
        assertThat(a.isPersisted()).isTrue();
        a.setName("foo");
        session.saveOrUpdate(a);
        session.flush();
    }
}
