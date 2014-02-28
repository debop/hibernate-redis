package org.hibernate.examples.tests.jpa.config

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import javax.persistence.{EntityManager, PersistenceContext}
import org.springframework.beans.factory.annotation.Autowired
import org.junit.Test
import org.scalatest.junit.JUnitSuite

/**
 * org.hibernate.examples.tests.jpa.config.JpaConfigurationTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:31
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[JpaConfiguration]))
class JpaConfigurationTest extends JUnitSuite {

    @PersistenceContext
    var em: EntityManager = _

    @Autowired
    var repository: JpaAccountRepository = _

    @Test
    def configurationTest() {
        assert(em != null)
        assert(repository != null)
    }
}
