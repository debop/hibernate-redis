package org.hibernate.examples.mapping.inheritance.unionsubclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * org.hibernate.examples.mapping.inheritance.unionsubclass.CreditCard
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오전 1:33
 */
@Entity(name = "UnionSubsclass_CreditCard")
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class CreditCard extends AbstractBilling {

  private String companyName;

  @Column(nullable = false)
  private String number;

  @Column(nullable = false)
  private Integer expMonth;

  @Column(nullable = false)
  private Integer expYear;

  @Override
  public int hashCode() {
    return HashTool.compute(super.hashCode(), companyName, number);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("companyName", companyName)
        .add("number", number)
        .add("expMonth", expMonth)
        .add("expYear", expYear);
  }

  private static final long serialVersionUID = 1711294363190461204L;
}
