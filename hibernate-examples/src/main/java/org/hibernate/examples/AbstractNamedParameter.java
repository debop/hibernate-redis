package org.hibernate.examples;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.examples.model.AbstractValueObject;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

/**
 * Query 등에서 사용할 Named Parameter의 기본 클래스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:24
 */
@Getter
@Setter
public abstract class AbstractNamedParameter extends AbstractValueObject implements NamedParameter {

  private final String name;
  private Object value;

  protected AbstractNamedParameter(String name, Object value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name)
        .add("value", value);

  }

  private static final long serialVersionUID = 5196081474408493840L;
}
