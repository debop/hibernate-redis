package org.hibernate.examples.mapping.associations.onetoone;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.hibernate.examples.mapping.associations.onetoone.bidirectionalManyToOne.Husband;
import org.hibernate.examples.mapping.associations.onetoone.bidirectionalManyToOne.Wife;
import org.hibernate.examples.mapping.associations.onetoone.primarykey.OneToOneAuthor;
import org.hibernate.examples.mapping.associations.onetoone.primarykey.OneToOneBiography;
import org.hibernate.examples.mapping.associations.onetoone.unidirectionalManyToOne.Cavalier;
import org.hibernate.examples.mapping.associations.onetoone.unidirectionalManyToOne.Horse;
import org.hibernate.examples.mapping.associations.onetoone.unidirectionalOneToOne.Vehicle;
import org.hibernate.examples.mapping.associations.onetoone.unidirectionalOneToOne.Wheel;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.associations.onetoone.OneToOneTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 2:48
 */
@Slf4j
@Transactional
public class OneToOneTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  /**
   * one-to-one 매핑 중 가장 보편적인 방식입니다.
   */
  @Test
  @Rollback(false)
  public void authorBiography() throws Exception {
    OneToOneAuthor author = new OneToOneAuthor();
    author.setName("debop");

    author.getBiography().setInformation("Sunghyouk Bae");
    author.getPicture().setPath("file://a/b/c");

    em.persist(author);
    em.flush();
    em.clear();

    author = em.find(OneToOneAuthor.class, author.getId());
    assertThat(author).isNotNull();
    OneToOneBiography bio = author.getBiography();
    assertThat(bio).isNotNull();
    assertThat(bio.getInformation()).isEqualToIgnoringCase("Sunghyouk Bae");

    author.getBiography().setInformation("debop");
    em.persist(author);
    em.flush();
    em.clear();

    author = em.find(OneToOneAuthor.class, author.getId());
    assertThat(author).isNotNull();
    assertThat(author.getBiography().getInformation()).isEqualToIgnoringCase("debop");
  }

  @Test
  public void unidirectionalManyToOne() throws Exception {

    Horse horse = new Horse();
    horse.setName("Palefrenier");
    em.persist(horse);

    Cavalier cavalier = new Cavalier();
    cavalier.setName("Caroline");
    cavalier.setHorse(horse);
    em.persist(cavalier);

    em.flush();
    em.clear();

    cavalier = em.find(Cavalier.class, cavalier.getId());
    assertThat(cavalier).isNotNull();

    horse = cavalier.getHorse();
    assertThat(horse).isNotNull();

    em.remove(cavalier);
    em.remove(horse);
    em.flush();
  }

  @Test
  public void unidirectionalOneToOne() throws Exception {

    Vehicle vehicle = new Vehicle();
    vehicle.setBrand("Mercedes");

    Wheel wheel = new Wheel();
    wheel.setVehicle(vehicle);

    em.persist(vehicle);
    em.persist(wheel);
    em.flush();
    em.clear();

    log.debug("Wheel=[{}]", wheel);

    wheel = em.find(Wheel.class, wheel.getId());
    assertThat(wheel).isNotNull();

    vehicle = wheel.getVehicle();
    assertThat(vehicle).isNotNull();

    em.remove(wheel);
    em.remove(vehicle);
    em.flush();
  }

  @Test
  public void bidirectionalManyToOne() throws Exception {

    Husband husband = new Husband();
    husband.setName("Alex");

    Wife wife = new Wife();
    wife.setName("Bea");

    husband.setWife(wife);
    wife.setHusband(husband);
    em.persist(husband);
    em.persist(wife);
    em.flush();
    em.clear();

    husband = em.find(Husband.class, husband.getId());
    assertThat(husband).isNotNull();
    assertThat(husband.getWife()).isNotNull();
    em.clear();

    wife = em.find(Wife.class, wife.getId());
    assertThat(wife).isNotNull();

    husband = wife.getHusband();
    assertThat(husband).isNotNull();

    Wife bea2 = new Wife();
    em.persist(bea2);
    bea2.setName("Still Bea");

    husband.setWife(bea2);
    wife.setHusband(null);
    bea2.setHusband(husband);

    em.persist(husband);
    em.persist(bea2);
    em.flush();
    em.clear();


    husband = em.find(Husband.class, husband.getId());
    assertThat(husband).isNotNull();
    assertThat(husband.getWife()).isNotNull();
    assertThat(husband.getWife().getHusband()).isEqualTo(husband);

    em.clear();

    wife = em.find(Wife.class, wife.getId());
    assertThat(wife).isNotNull();
    assertThat(wife.getHusband()).isNull();
    em.remove(wife);

    bea2 = em.find(Wife.class, bea2.getId());
    assertThat(bea2).isNotNull();

    husband = bea2.getHusband();
    assertThat(husband).isNotNull();

    bea2.setHusband(null);
    husband.setWife(null);
    em.remove(husband);
    em.remove(wife);
    em.flush();
  }

}
