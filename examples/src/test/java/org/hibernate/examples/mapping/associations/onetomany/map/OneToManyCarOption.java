package org.hibernate.examples.mapping.associations.onetomany.map;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.examples.model.AbstractValueObject;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Embeddable;

/**
 * org.hibernate.examples.mapping.associations.onetomany.map.OneToManyCarOption
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 1:25
 */
@Embeddable
@Getter
@Setter
public class OneToManyCarOption extends AbstractValueObject {

  public OneToManyCarOption() {
  }

  public OneToManyCarOption(String name, Integer value) {
    this.name = name;
    this.value = value;
  }

  private String name;

  private Integer value;

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

  private static final long serialVersionUID = -4017716295243845509L;
}
