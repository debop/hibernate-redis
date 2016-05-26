package org.hibernate.examples.mapping.queries;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.queries.JpaQueryTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 8:11
 */
@Slf4j
@Transactional
public class JpaQueryTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void simpleQueries() throws Exception {

    String hypothesisName = Hypothesis.class.getName();

    assertQuery(em, 4, em.createQuery("select h from Hypothesis h"));
    assertQuery(em, 4, em.createQuery("select h from " + hypothesisName + " h"));
    assertQuery(em, 1, em.createQuery("select h from Helicopter h"));
    // assertQuery(em, 5, em.createQuery("select o from java.lang.Object o"));
  }

  @Test
  public void testConstantParameterQueries() throws Exception {
    Query query = em.createQuery("select h from Hypothesis h where h.description = 'stuff works'");
    assertQuery(em, 1, query);
  }

  @Test
  public void testParametricQueries() throws Exception {
    Query query = em
        .createQuery("select h from Hypothesis h where h.description = :myParam")
        .setParameter("myParam", "stuff works");
    assertQuery(em, 1, query);
  }

  //@QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
  private void assertQuery(final EntityManager em, final int expectedSize, final Query testedQuery) {
    assertThat(testedQuery.getResultList()).as("Query failed").hasSize(expectedSize);
    em.clear();
  }

  @Before
  public void setUp() throws Exception {
    em.getEntityManagerFactory().getCache().evict(Hypothesis.class);
    em.getEntityManagerFactory().getCache().evict(Helicopter.class);

    log.debug("예제용 데이터 추가");

    Hypothesis socrates = new Hypothesis();
    socrates.setId("13");
    socrates.setDescription("There are more than two dimensions over the shadows we see out of the cave");
    socrates.setPosition(1);
    em.persist(socrates);

    Hypothesis peano = new Hypothesis();
    peano.setId("14");
    peano.setDescription("Peano's curve and then Hilbert's space filling curve proof the connection from mono-dimensional to bi-dimensional space");
    peano.setPosition(2);
    em.persist(peano);

    Hypothesis sanne = new Hypothesis();
    sanne.setId("15");
    sanne.setDescription("Hilbert's proof of connection to 2 dimensions can be induced to reason on N dimensions");
    sanne.setPosition(3);
    em.persist(sanne);

    Hypothesis shortOne = new Hypothesis();
    shortOne.setId("16");
    shortOne.setDescription("stuff works");
    shortOne.setPosition(4);
    em.persist(shortOne);

    Helicopter helicopter = new Helicopter();
    helicopter.setName("No creative clue ");
    em.persist(helicopter);

    em.flush();
    em.clear();
  }
}
