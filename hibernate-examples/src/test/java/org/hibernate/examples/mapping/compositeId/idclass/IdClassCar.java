package org.hibernate.examples.mapping.compositeId.idclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.examples.model.AbstractValueObject;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * org.hibernate.examples.mapping.compositeId.idclass.IdClassCar
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 4:35
 */
@Entity
//@Cache(region = "composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@IdClass(CarIdentifier.class)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class IdClassCar extends AbstractValueObject {

  @Id
  private String brand;

  @Id
  private int year;

  private String serialNo;

  @Override
  public int hashCode() {
    return HashTool.compute(brand, year);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
                .add("brand", brand)
                .add("year", year)
                .add("serialNo", serialNo);
  }

  private static final long serialVersionUID = -2144857053612105427L;
}
