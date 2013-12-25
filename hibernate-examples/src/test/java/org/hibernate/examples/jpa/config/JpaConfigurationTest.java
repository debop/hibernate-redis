package org.hibernate.examples.jpa.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.examples.jpa.config.JpaConfigurationTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 10:36
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaConfiguration.class })
public class JpaConfigurationTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    JpaAccountRepository repository;

    @Test
    @Transactional
    public void configurationTest() throws Exception {
        assertThat(em).isNotNull();
        assertThat(repository).isNotNull();
    }

    @Test
    @Transactional
    public void crudTest() throws Exception {
        JpaAccount account = new JpaAccount();
        account.setName("Sunghyouk Bae");
        account.setCashBalance(100.0);
        account = repository.saveAndFlush(account);
        em.clear();

        JpaAccount loaded = repository.findOne(account.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded).isEqualTo(account);
        assertThat(loaded.isPersisted()).isTrue();

        repository.delete(loaded);
        em.flush();

        assertThat(repository.count()).isEqualTo(0);
    }
}
