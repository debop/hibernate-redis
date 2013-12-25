package org.hibernate.examples.mapping;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.config.JpaHSqlConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManagerFactory;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.JpaMappingTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 4:01
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JpaHSqlConfiguration.class })
public class JpaMappingTest {

    @Autowired EntityManagerFactory emf;

    @Test
    public void mapping() throws Exception {
        assertThat(emf).isNotNull();
    }
}
