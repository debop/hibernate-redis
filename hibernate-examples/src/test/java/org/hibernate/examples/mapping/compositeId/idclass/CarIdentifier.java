package org.hibernate.examples.mapping.compositeId.idclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.examples.model.AbstractValueObject;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

/**
 * 브랜드 명과 연식으로 자동차의 Identifier 값으로 표현합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 4:32
 */
@Getter
@Setter
public class CarIdentifier extends AbstractValueObject {

  private String brand;
  private int year;

  protected CarIdentifier() {}

  public CarIdentifier(String brand, int year) {
    this.brand = brand;
    this.year = year;
  }

  @Override
  public int hashCode() {
    return HashTool.compute(brand, year);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
                .add("brand", brand)
                .add("year", year);
  }

  private static final long serialVersionUID = -8248095183925181094L;
}
