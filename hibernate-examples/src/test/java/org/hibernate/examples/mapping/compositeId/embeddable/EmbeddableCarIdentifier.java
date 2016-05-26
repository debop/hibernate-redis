package org.hibernate.examples.mapping.compositeId.embeddable;

import lombok.Getter;
import org.hibernate.examples.model.AbstractValueObject;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * org.hibernate.examples.mapping.compositeId.embeddable.EmbeddableCarIdentifier
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 4:40
 */
@Embeddable
@Getter
public class EmbeddableCarIdentifier extends AbstractValueObject {

  @Column(name = "brand", nullable = false, length = 32)
  private String brand;

  @Column(name = "releaseYear", nullable = false)
  private int year;

  protected EmbeddableCarIdentifier() {}

  public EmbeddableCarIdentifier(String brand, int year) {
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

  private static final long serialVersionUID = -1715963785986881704L;
}
