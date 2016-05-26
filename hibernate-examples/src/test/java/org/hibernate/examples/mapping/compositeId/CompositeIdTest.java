package org.hibernate.examples.mapping.compositeId;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.hibernate.examples.mapping.compositeId.embeddable.EmbeddableCarIdentifier;
import org.hibernate.examples.mapping.compositeId.embeddable.EmbeddableIdCar;
import org.hibernate.examples.mapping.compositeId.idclass.CarIdentifier;
import org.hibernate.examples.mapping.compositeId.idclass.IdClassCar;
import org.hibernate.examples.mapping.compositeId.manytoone.Order;
import org.hibernate.examples.mapping.compositeId.manytoone.OrderDetail;
import org.hibernate.examples.mapping.compositeId.manytoone.Product;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FIXME: FST cause error in CompositeId entities
 * FIXME: reason: Class java.lang.reflect.Field does not implement Serializable or externalizable
 * FIXME: I have no idea ...
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 4:38
 */
@Slf4j
@Transactional
public class CompositeIdTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void embeddedidTest() {
    EmbeddableIdCar car = new EmbeddableIdCar(new EmbeddableCarIdentifier("Kia", 2012));
    car.setSerialNo("6675");

    em.persist(car);
    em.flush();
    em.clear();

    EmbeddableIdCar loaded = em.find(EmbeddableIdCar.class, car.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getId()).isNotNull();
    assertThat(loaded.getId().getBrand()).isEqualTo("Kia");

    loaded.setSerialNo("5081");
    em.persist(loaded);
    em.flush();
    em.clear();

    loaded = em.find(EmbeddableIdCar.class, new EmbeddableCarIdentifier("Kia", 2012));

    assertThat(loaded).isNotNull();

    em.remove(loaded);
    em.flush();
    em.clear();

    loaded = em.find(EmbeddableIdCar.class, new EmbeddableCarIdentifier("Kia", 2012));
    assertThat(loaded).isNull();
  }

  @Test
  public void idClassTest() {
    IdClassCar car = new IdClassCar();
    car.setBrand("Kia");
    car.setYear(2012);
    car.setSerialNo("6675");

    em.persist(car);
    em.flush();
    em.clear();

    IdClassCar loaded = em.find(IdClassCar.class, new CarIdentifier("Kia", 2012));

    assertThat(loaded).isNotNull();
    assertThat(loaded.getBrand()).isNotNull();
    assertThat(loaded.getBrand()).isEqualTo("Kia");

    loaded.setSerialNo("5081");
    em.persist(loaded);
    em.flush();
    em.clear();

    loaded = em.find(IdClassCar.class, new CarIdentifier("Kia", 2012));

    assertThat(loaded).isNotNull();
    assertThat(loaded.getBrand()).isNotNull();
    assertThat(loaded.getBrand()).isEqualTo("Kia");

    em.remove(loaded);
    em.flush();
    em.clear();

    loaded = em.find(IdClassCar.class, new CarIdentifier("Kia", 2012));
    assertThat(loaded).isNull();
  }

  @Test
  public void manytoone() throws Exception {
    Order order = new Order();
    order.setNumber("order-1234");
    em.persist(order);

    Product product = new Product();
    product.setName("monitor");
    em.persist(product);

    OrderDetail detail = new OrderDetail(order, product);
    order.getOrderDetails().add(detail);
    product.getOrderDetails().add(detail);

    em.persist(detail);
    em.flush();
    em.clear();

    OrderDetail loaded = em.find(OrderDetail.class, detail.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded.getId().getOrder()).isNotNull();
    assertThat(loaded.getId().getProduct()).isNotNull();

    loaded.setQuantity(12);
    em.persist(loaded);
    em.flush();
    em.clear();

    loaded = em.find(OrderDetail.class, detail.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getId().getOrder()).isNotNull();
    assertThat(loaded.getId().getProduct()).isNotNull();

    em.remove(loaded);
    em.flush();
    em.clear();
  }
}
