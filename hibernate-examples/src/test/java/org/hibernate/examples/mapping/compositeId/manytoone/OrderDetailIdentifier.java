package org.hibernate.examples.mapping.compositeId.manytoone;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.examples.model.AbstractValueObject;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * org.hibernate.examples.mapping.compositeId.manytoone.OrderDetailIdentifier
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 5:06
 */
@Embeddable
@Getter
@Setter
public class OrderDetailIdentifier extends AbstractValueObject implements java.io.Serializable {

  protected OrderDetailIdentifier() {}

  public OrderDetailIdentifier(Order order, Product product) {
    assert (order != null);
    assert (product != null);

    this.order = order;
    this.product = product;
  }

  @ManyToOne
  @JoinColumn(name = "orderId")
  private Order order;

  @ManyToOne
  @JoinColumn(name = "productId")
  private Product product;

  @Override
  public int hashCode() {
    return HashTool.compute(order, product);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("orderId", order.getId())
        .add("productId", product.getId());
  }

  private static final long serialVersionUID = -7914201856753998776L;
}
