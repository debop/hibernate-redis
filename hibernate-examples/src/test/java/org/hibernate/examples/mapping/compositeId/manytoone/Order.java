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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * org.hibernate.examples.mapping.compositeId.manytoone.Order
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 5:07
 */
@Entity
@Table(name = "CompositeId_Order")
//@Cache(region = "composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Order extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "orderId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String number;

  @Temporal(TemporalType.DATE)
  private Date orderDate;

  @OneToMany(mappedBy = "id.order", cascade = {CascadeType.ALL}, orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.EXTRA)
  private List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

  @Override
  public int hashCode() {
    return HashTool.compute(number);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
                .add("number", number)
                .add("orderDate", orderDate);
  }

  private static final long serialVersionUID = -478214079111379653L;
}
