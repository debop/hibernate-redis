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
 * 상속된 클래스는 독립된 테이블을 가지므로 not null을 지정할 수 있다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오전 12:30
 */
@Entity(name = "UnionSubsclass_BankAccount")
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class BankAccount extends AbstractBilling {

  @Column(nullable = false)
  private String account;

  @Column(nullable = false)
  private String bankname;

  private String swift;

  @Override
  public int hashCode() {
    return HashTool.compute(super.hashCode(), account, bankname);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("account", account)
        .add("bankname", bankname)
        .add("swift", swift);
  }

  private static final long serialVersionUID = 120306592391268140L;
}
