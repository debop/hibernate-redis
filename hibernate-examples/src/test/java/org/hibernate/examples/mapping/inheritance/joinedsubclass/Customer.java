package org.hibernate.examples.mapping.inheritance.joinedsubclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * org.hibernate.examples.mapping.inheritance.joinedsubclass.Customer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 30. 오후 12:54
 */
@Entity(name = "JoinedSubclass_Customer")
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Customer extends Person {

  private String mobile;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contactEmployeeId", nullable = false)
  @LazyToOne(LazyToOneOption.PROXY)
  private Employee contactEmployee;

  @Override
  public int hashCode() {
    return HashTool.compute(mobile);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("mobile", mobile);
  }

  private static final long serialVersionUID = 8943550440998265187L;
}
