package org.hibernate.examples.mapping.associations.onetomany.list;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * org.hibernate.examples.mapping.associations.onetomany.list.OneToManyOrder
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 1:17
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class OneToManyOrder extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String no;

  // inverse=true (mappedBy="order")
  @OneToMany(mappedBy = "order", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.EXTRA)
  private List<OneToManyOrderItem> items = new ArrayList<OneToManyOrderItem>();

  @Override
  public int hashCode() {
    return HashTool.compute(no);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
                .add("no", no);
  }

  private static final long serialVersionUID = 6377149156636760165L;
}
