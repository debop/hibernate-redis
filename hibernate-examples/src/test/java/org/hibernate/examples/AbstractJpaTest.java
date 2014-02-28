package org.hibernate.examples;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.config.JpaH2Configuration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManagerFactory;

/**
 * org.hibernate.examples.AbstractJpaTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 5:45
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaH2Configuration.class })
public abstract class AbstractJpaTest {

    @Autowired
    protected EntityManagerFactory emf;
}
