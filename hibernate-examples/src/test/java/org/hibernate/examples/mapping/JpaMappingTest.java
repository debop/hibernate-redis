package org.hibernate.examples.mapping;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManagerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.JpaMappingTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 4:01
 */
@Slf4j
public class JpaMappingTest extends AbstractJpaTest {

  @Autowired
  EntityManagerFactory emf;

  @Test
  public void mapping() throws Exception {
    assertThat(emf).isNotNull();
  }
}
