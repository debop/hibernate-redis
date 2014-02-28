package org.hibernate.examples;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.examples.config.HibernateH2Configuration;
import org.hibernate.examples.hibernate.repository.HibernateDao;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * org.hibernate.examples.AbstractHibernateTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 5:37
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateH2Configuration.class })
public abstract class AbstractHibernateTest {

    @Autowired
    protected SessionFactory sessionFactory;

    @Autowired
    protected HibernateDao dao;
}
