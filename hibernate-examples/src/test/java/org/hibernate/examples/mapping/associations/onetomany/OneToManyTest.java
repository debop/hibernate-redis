package org.hibernate.examples.mapping.associations.onetomany;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * org.hibernate.examples.mapping.associations.onetomany.OneToManyTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 2:28
 */
@Slf4j
@Transactional
public class OneToManyTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void mapAndElementCollection() throws Exception {

    OneToManyAddress home = new OneToManyAddress();
    home.setCity("Paris");

    OneToManyAddress work = new OneToManyAddress();
    work.setCity("San Francisco");

    OneToManyUser user = new OneToManyUser();
    user.getAddresses().put("home", home);
    user.getAddresses().put("work", work);
    user.getNicknames().add("idrA");
    user.getNicknames().add("day[9]");

    em.persist(home);
    em.persist(work);
    em.persist(user);

    OneToManyUser user2 = new OneToManyUser();
    user2.getNicknames().add("idrA");
    user2.getNicknames().add("day[9]");

    em.persist(user2);
    em.flush();
    em.clear();

    user = em.find(OneToManyUser.class, user.getId());

    assertThat(user.getNicknames()).as("Should have 2 nick1").hasSize(2);
    assertThat(user.getNicknames()).as("Should contain nicks").contains("idrA", "day[9]");

    user.getNicknames().remove("idrA");
    user.getAddresses().remove("work");

    em.persist(user);
    em.flush();
    em.clear();

    user = em.find(OneToManyUser.class, user.getId());

    // TODO do null value
    assertThat(user.getAddresses()).as("List should have 1 elements").hasSize(1);
    assertThat(user.getAddresses().get("home").getCity()).as("home address should be under home").isEqualTo(home.getCity());
    assertThat(user.getNicknames()).as("Should have 1 nick1").hasSize(1);
    assertThat(user.getNicknames()).as("Should contain nick").contains("day[9]");

    em.remove(user);
    // CascadeType.ALL 로 user 삭제 시 address 삭제 됨
    // em.srem(em.load(Address.class, home.getId()));
    // em.srem(em.load(Address.class, work.getId()));

    user2 = em.find(OneToManyUser.class, user2.getId());
    assertThat(user2.getNicknames()).as("Should have 2 nicks").hasSize(2);
    assertThat(user2.getNicknames()).as("Should contain nick").contains("idrA", "day[9]");
    em.remove(user2);
    em.flush();
  }
}
