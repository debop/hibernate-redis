package org.hibernate.examples.mapping.associations.onetomany.set;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.associations.onetomany.set.OneToManySetTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 2:12
 */
@Slf4j
@Transactional
public class OneToManySetTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void bidding() {
    OneToManyBiddingItem item = new OneToManyBiddingItem();
    OneToManyBid bid1 = new OneToManyBid(item, new BigDecimal(100.0));
    OneToManyBid bid2 = new OneToManyBid(item, new BigDecimal(200.0));

    em.persist(item);
    em.flush();
    em.clear();

    item = em.find(OneToManyBiddingItem.class, item.getId());
    assertThat(item).isNotNull();
    assertThat(item.getBids().size()).isEqualTo(2);

    bid1 = item.getBids().iterator().next();
    item.getBids().remove(bid1);

    em.persist(item);
    em.flush();
    em.clear();

    item = em.find(OneToManyBiddingItem.class, item.getId());
    assertThat(item).isNotNull();
    assertThat(item.getBids()).hasSize(1);
  }

  @Test
  public void productTest() {

    ProductItem item = new ProductItem();
    item.setName("item");

    ProductImage image1 = new ProductImage();
    image1.setItem(item);
    image1.setName("image1");
    item.getImages().add(image1);

    ProductImage image2 = new ProductImage();
    image2.setItem(item);
    image2.setName("image2");
    item.getImages().add(image2);

    em.persist(item);
    em.flush();
    em.clear();

    ProductItem loaded = em.find(ProductItem.class, item.getId());
    assertThat(loaded.getImages()).hasSize(2);

    loaded.getImages().clear();

    em.persist(loaded);
    em.flush();
    em.clear();

    loaded = em.find(ProductItem.class, item.getId());
    assertThat(loaded.getImages()).hasSize(0);
  }
}
