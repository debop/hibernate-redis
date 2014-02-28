package org.hibernate.examples.tests

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.hibernate.examples.tests.config.JpaH2Configuration
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManagerFactory

/**
 * org.hibernate.examples.tests.AbstractJpaTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:12
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[JpaH2Configuration]))
abstract class AbstractJpaTest {

    @Autowired
    val emf: EntityManagerFactory
}
