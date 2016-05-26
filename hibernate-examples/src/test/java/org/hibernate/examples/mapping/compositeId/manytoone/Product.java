package org.hibernate.examples.mapping.compositeId.manytoone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * org.hibernate.examples.mapping.compositeId.manytoone.Product
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 5:07
 */
@Entity
@Table(name = "CompositeId_Product")
//@Cache(region = "composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Product extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "productId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  @Column(name = "productName")
  private String name;

  @OneToMany(mappedBy = "id.product", cascade = {CascadeType.ALL}, orphanRemoval = true)
  @LazyCollection(value = LazyCollectionOption.EXTRA)
  private Set<OrderDetail> orderDetails = new HashSet<OrderDetail>();

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name);
  }

  private static final long serialVersionUID = -4725360631652953447L;
}
