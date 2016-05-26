package org.hibernate.examples.mapping.associations.manytoone;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.associations.manytoone.ManyToOneTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오전 9:47
 */
@Slf4j
@Transactional
public class ManyToOneTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  @Transactional
  public void bidirectionalManyToOneInsertUpdateFalse() throws Exception {

    BeerVendor vendor = new BeerVendor();
    vendor.setName("Heineken");
    em.persist(vendor);

    Brewery hoeBrewery = new Brewery();
    Beer hoegaarden = new Beer();
    hoeBrewery.getBeers().add(hoegaarden);
    hoegaarden.setBrewery(hoeBrewery);
    hoeBrewery.setVendor(vendor);

    em.persist(hoeBrewery);
    em.flush();
    em.clear();

    hoegaarden = em.find(Beer.class, hoegaarden.getId());
    assertThat(hoegaarden).isNotNull();
    // load ManyToOne
    assertThat(hoegaarden.getBrewery()).isNotNull();
    assertThat(hoegaarden.getBrewery().getId()).isNotNull();
    assertThat(hoegaarden.getBrewery().getBeers()).hasSize(1).containsOnly(hoegaarden);
    assertThat(hoegaarden.getBrewery().getVendor()).isNotNull();
    assertThat(hoegaarden.getBrewery().getVendor().getId()).isNotNull();

    // citron을 명시적으로 저장하지 않은 것은, Brewery#beers @OneToMany에 mappedBy 를 지정하지 않았기 때문에 전체를 한번에 추가 삭제합니다.
    // 만약 Brewery#beers에 mappedBy="brewery" 를 추가한다면, Beer 엔티티 개별로 조작해야 하므로, citron은 미리 저장해야 합니다.
    // 또한 Beer#brewery의 cascadeType도 MERGE|PERSIST|REFRESH 를 추가해야 합니다.
    Beer citron = new Beer();
    hoeBrewery = hoegaarden.getBrewery();
    hoeBrewery.getBeers().remove(hoegaarden);
    hoeBrewery.getBeers().add(citron);
    citron.setBrewery(hoeBrewery);
    // ManyToOne 의 cascadeType 에 CascadeType.PERSIST 가 있으므로, hoeBrewery도 update 된다.
    em.remove(hoegaarden);
    em.flush();
    em.clear();

    citron = em.find(Beer.class, citron.getId());
    assertThat(citron.getBrewery().getBeers()).hasSize(1).containsOnly(citron);
    hoeBrewery = citron.getBrewery();
    citron.setBrewery(null);
    hoeBrewery.getBeers().clear();
    em.remove(citron);
    em.remove(hoeBrewery);
    em.flush();
  }

  @Test
  public void uniDirectionalManyToOne() {

    Jug jug = new Jug("JUG Summer Camp");

    JugMember emmanuel = new JugMember("Emmanuel Bernard");
    emmanuel.setMemberOf(jug);

    JugMember jerome = new JugMember("Jerome");
    jerome.setMemberOf(jug);

    em.persist(jug);
    em.persist(emmanuel);
    em.persist(jerome);
    em.flush();
    em.clear();

    emmanuel = em.find(JugMember.class, emmanuel.getId());
    assertThat(emmanuel).isNotNull();
    assertThat(emmanuel.getMemberOf()).isNotNull();
    assertThat(emmanuel.getMemberOf()).isEqualTo(jug);

    em.remove(emmanuel);
    em.flush();
    em.clear();

    jerome = em.find(JugMember.class, jerome.getId());
    assertThat(jerome.getMemberOf()).isNotNull();
    assertThat(jerome.getMemberOf()).isEqualTo(jug);
    em.remove(jerome);
    em.flush();
    em.clear();

    jug = em.find(Jug.class, jug.getId());
    assertThat(jug).isNotNull();

    em.remove(jug);
    em.flush();
  }

  @Test
  public void bidirectionalManyToOneRegular() throws Exception {

    SalesForce force = new SalesForce("Red Hat");
    em.persist(force);

    SalesGuy eric = new SalesGuy();
    eric.setName("Eric");
    eric.setSalesForce(force);
    force.getSalesGuys().add(eric);
    em.persist(eric);

    SalesGuy simon = new SalesGuy();
    simon.setName("Simon");
    simon.setSalesForce(force);
    force.getSalesGuys().add(simon);
    em.persist(simon);

    em.flush();
    em.clear();

    force = em.find(SalesForce.class, force.getId());
    assertThat(force).isNotNull();
    assertThat(force.getSalesGuys()).isNotNull();
    assertThat(force.getSalesGuys()).hasSize(2);
    for (SalesGuy guy : force.getSalesGuys()) {
      assertThat(guy.getId()).isNotNull();
    }

    simon = em.find(SalesGuy.class, simon.getId());

    // Cascade 때문에
    force.getSalesGuys().remove(simon);
    em.remove(simon);
    em.flush();
    em.clear();

    force = em.find(SalesForce.class, force.getId());
    assertThat(force).isNotNull();
    assertThat(force.getSalesGuys()).isNotNull();
    assertThat(force.getSalesGuys()).hasSize(1);

    em.remove(force.getSalesGuys().iterator().next());
    em.remove(force);
    em.flush();
  }
}
