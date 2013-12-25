package org.hibernate.examples.mapping;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.examples.config.HibernateHSqlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.HibernateMappingTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:42
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateHSqlConfiguration.class })
public class HibernateMappingTest {

    @Autowired
    SessionFactory sessionFactory;

    @Test
    public void mappingTest() throws Exception {
        assertThat(sessionFactory).isNotNull();
    }

}
