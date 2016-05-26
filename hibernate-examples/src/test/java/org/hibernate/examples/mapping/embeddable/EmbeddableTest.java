package org.hibernate.examples.mapping.embeddable;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.embeddable.EmbeddableTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 30. 오후 12:38
 */
@Slf4j
@Transactional
public class EmbeddableTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void embeddableTest() throws Exception {
    User user = new User("debop", "1234");

    user.setFirstname("성혁");
    user.setLastname("배");

    user.getHomeAddress().setCity("서울");
    user.getHomeAddress().setStreet("정릉로");
    user.getHomeAddress().setZipcode("100-100");

    user.getOfficeAddress().setCity("서울");
    user.getOfficeAddress().setStreet("안국로");
    user.getOfficeAddress().setZipcode("200-200");

    em.persist(user);
    em.flush();
    em.clear();

    User loaded = em.find(User.class, user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getHomeAddress()).isNotNull();
    assertThat(loaded.getHomeAddress().getZipcode()).isEqualTo(user.getHomeAddress().getZipcode());
    assertThat(loaded.getOfficeAddress()).isNotNull();
    assertThat(loaded.getOfficeAddress().getZipcode()).isEqualTo(user.getOfficeAddress().getZipcode());
  }
}
