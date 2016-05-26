package org.hibernate.examples.mapping.queries;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.examples.AbstractHibernateTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.queries.HibernateQueryTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 8:25
 */
@Slf4j
@Transactional
public class HibernateQueryTest extends AbstractHibernateTest {

  public Session getSession() {
    return sessionFactory.getCurrentSession();
  }

  @Test
  public void simpleQueries() throws Exception {

    String hypothesiFullName = Hypothesis.class.getName();

    Session session = getSession();
    assertQuery(session, 4, session.createQuery("from Hypothesis"));
    assertQuery(session, 4, session.createQuery("from " + hypothesiFullName));
    assertQuery(session, 1, session.createQuery("from Helicopter"));
    // assertQuery(session, 5, session.createQuery("from java.lang.Object"));
  }

  @Test
  public void testConstantParameterQueries() throws Exception {
    final Session session = getSession();

    Query query = session
        .createQuery("from Hypothesis h where h.description = 'stuff works'")
        .setCacheable(true);
    assertQuery(session, 1, query);
  }

  @Test
  public void testParametricQueries() throws Exception {
    final Session session = getSession();

    Query query = session
        .createQuery("from Hypothesis h where h.description = :myParam")
        .setString("myParam", "stuff works")
        .setCacheable(true);
    assertQuery(session, 1, query);
  }

  private void assertQuery(final Session session, final int expectedSize, final Query testedQuery) {
    assertThat(testedQuery.list()).hasSize(expectedSize).as("Query failed");
    session.clear();
  }

  @Before
  public void setUp() throws Exception {
    sessionFactory.getCache().evictEntityRegion(Hypothesis.class);
    sessionFactory.getCache().evictEntityRegion(Helicopter.class);

    log.info("예제용 데이터 추가");

    Hypothesis socrates = new Hypothesis();
    socrates.setId("13");
    socrates.setDescription("There are more than two dimensions over the shadows we see out of the cave");
    socrates.setPosition(1);
    getSession().saveOrUpdate(socrates);

    Hypothesis peano = new Hypothesis();
    peano.setId("14");
    peano.setDescription("Peano's curve and then Hilbert's space filling curve proof the connection from mono-dimensional to bi-dimensional space");
    peano.setPosition(2);
    getSession().saveOrUpdate(peano);

    Hypothesis sanne = new Hypothesis();
    sanne.setId("15");
    sanne.setDescription("Hilbert's proof of connection to 2 dimensions can be induced to reason on N dimensions");
    sanne.setPosition(3);
    getSession().saveOrUpdate(sanne);

    Hypothesis shortOne = new Hypothesis();
    shortOne.setId("16");
    shortOne.setDescription("stuff works");
    shortOne.setPosition(4);
    getSession().saveOrUpdate(shortOne);

    Helicopter helicopter = new Helicopter();
    helicopter.setName("No creative clue ");
    getSession().saveOrUpdate(helicopter);

    getSession().flush();
    getSession().clear();
  }
}
