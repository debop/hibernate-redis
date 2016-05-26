package org.hibernate.examples.mapping.associations.onetomany.set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parent;
import org.hibernate.examples.model.AbstractValueObject;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Embeddable;

/**
 * org.hibernate.examples.mapping.associations.onetomany.set.ProductImage
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 2:18
 */
@Embeddable
@Getter
@Setter
public class ProductImage extends AbstractValueObject {

  // Component가 소유자 Entity를 양방향으로 연관지을 때 사용합니다.
  @Parent
  private ProductItem item;

  private String name;

  private String filename;

  private Integer sizeX;
  private Integer sizeY;

  @Override
  public int hashCode() {
    return HashTool.compute(name, filename);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name)
        .add("filename", filename);
  }

  private static final long serialVersionUID = 6488204424105012791L;
}
