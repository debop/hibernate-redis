package org.hibernate.examples.mapping.inheritance.joinedsubclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;

/**
 * org.hibernate.examples.mapping.inheritance.joinedsubclass.Employee
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 30. 오후 12:54
 */
@Entity(name = "JoinedSubclass_Employee")
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Employee extends Person {

  @Column(name = "empNo", nullable = false)
  private String empNo;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "companyId")
  @LazyToOne(LazyToOneOption.PROXY)
  private Company company;

  @Override
  public int hashCode() {
    return HashTool.compute(super.hashCode(), empNo, company);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("empNo", empNo)
        .add("company", company);
  }

  private static final long serialVersionUID = 5338078892200915069L;
}
