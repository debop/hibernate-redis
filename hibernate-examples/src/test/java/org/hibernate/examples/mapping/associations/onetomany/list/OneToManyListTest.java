package org.hibernate.examples.mapping.associations.onetomany.list;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.associations.onetomany.list.OneToManyListTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 1:22
 */
@Slf4j
@Transactional
public class OneToManyListTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void simpleOneToMany() throws Exception {
    OneToManyOrder order = new OneToManyOrder();
    OneToManyOrderItem item1 = new OneToManyOrderItem();
    item1.setName("Item1");
    item1.setOrder(order);
    order.getItems().add(item1);
    OneToManyOrderItem item2 = new OneToManyOrderItem();
    item2.setName("Item1");
    item2.setOrder(order);
    order.getItems().add(item2);

    em.persist(order);
    em.flush();
    em.clear();

    order = em.find(OneToManyOrder.class, order.getId());
    assertThat(order.getItems()).hasSize(2);

    OneToManyOrderItem item = order.getItems().iterator().next();
    order.getItems().remove(item);
    em.persist(order);
    em.flush();
    em.clear();

    order = em.find(OneToManyOrder.class, order.getId());
    assertThat(order.getItems()).hasSize(1);
  }

  @Test
  public void orderedList() throws Exception {
    OneToManyChild luke = new OneToManyChild("luke");
    OneToManyChild leia = new OneToManyChild("leia");
    em.persist(luke);
    em.persist(leia);

    OneToManyFather father = new OneToManyFather();
    father.getChildren().add(luke);
    father.getChildren().add(null);
    father.getChildren().add(leia);

    em.persist(father);
    em.flush();
    em.clear();

    father = em.find(OneToManyFather.class, father.getId());

    assertThat(father.getChildren())
        .as("List should have 3 elements")
        .hasSize(3);

    assertThat(father.getChildren().get(0).getName())
        .as("Luke should be first")
        .isEqualTo(luke.getName());

    assertThat(father.getChildren().get(1))
        .as("Second born should be null")
        .isNull();

    assertThat(father.getChildren().get(2).getName())
        .as("Leia should be third")
        .isEqualTo(leia.getName());


    em.remove(father);
    em.flush();
  }
}
