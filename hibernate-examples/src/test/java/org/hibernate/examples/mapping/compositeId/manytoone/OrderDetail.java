package org.hibernate.examples.mapping.compositeId.manytoone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * org.hibernate.examples.mapping.compositeId.manytoone.OrderDetail
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 5:07
 */
@Entity
@Table(name = "CompositeId_OrderDetail")
//@Cache(region = "composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OrderDetail extends AbstractHibernateEntity<OrderDetailIdentifier> {

  protected OrderDetail() {}

  public OrderDetail(Order order, Product product) {
    this.id = new OrderDetailIdentifier(order, product);
  }

  public OrderDetail(OrderDetailIdentifier id) {
    this.id = id;
  }

  @EmbeddedId
  @Setter(AccessLevel.PROTECTED)
  private OrderDetailIdentifier id;

  private BigDecimal unitPrice;
  private Integer quantity;
  private Float discount;


  @Override
  public int hashCode() {
    return HashTool.compute(unitPrice, quantity, discount);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
                .add("unitPrice", unitPrice)
                .add("quantity", quantity)
                .add("discount", discount);
  }

  private static final long serialVersionUID = 6958616166017033341L;
}
