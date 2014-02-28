package org.hibernate.examples.tests

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.hibernate.examples.tests.config.HibernateH2Configuration
import org.springframework.beans.factory.annotation.Autowired
import org.hibernate.SessionFactory
import org.hibernate.examples.hibernate.repository.HibernateDao

/**
 * org.hibernate.examples.tests.AbstractJpaTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:12
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[HibernateH2Configuration]))
abstract class AbstractHibernateTest {

    @Autowired val sessionFactory: SessionFactory

    @Autowired val dao: HibernateDao
}
