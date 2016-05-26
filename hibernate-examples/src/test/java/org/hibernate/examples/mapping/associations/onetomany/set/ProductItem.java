package org.hibernate.examples.mapping.associations.onetomany.set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * org.hibernate.examples.mapping.associations.onetomany.set.ProductItem
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 2:15
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class ProductItem extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;
  private String description;
  private BigDecimal initialPrice;
  private BigDecimal reservePrice;

  @Temporal(TemporalType.DATE)
  private Date startDate;

  @Temporal(TemporalType.DATE)
  private Date endDate;

  @CollectionTable(name = "ProductItemImages", joinColumns = {@JoinColumn(name = "productItemId")})
  @ElementCollection(targetClass = ProductImage.class)
  @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
  private Set<ProductImage> images = new HashSet<ProductImage>();

  public boolean removeImage(ProductImage image) {
    return images.remove(image);
  }

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name)
        .add("initialPrice", initialPrice)
        .add("reservePrice", reservePrice);
  }

  private static final long serialVersionUID = 9176636190484261550L;
}
