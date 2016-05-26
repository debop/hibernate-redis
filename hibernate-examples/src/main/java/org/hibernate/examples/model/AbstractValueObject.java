package org.hibernate.examples.model;

import org.hibernate.examples.utils.ToStringHelper;

/**
 * Value Object 의 기본 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 */
public abstract class AbstractValueObject implements ValueObject {

  @Override
  public boolean equals(Object obj) {
    return (obj != null) && (getClass().equals(obj.getClass())) && (hashCode() == obj.hashCode());
  }

  @Override
  public String toString() {
    return buildStringHelper().toString();
  }

  public ToStringHelper buildStringHelper() {
    return ToStringHelper.create(this);
  }

  private static final long serialVersionUID = 529523546260095342L;
}
