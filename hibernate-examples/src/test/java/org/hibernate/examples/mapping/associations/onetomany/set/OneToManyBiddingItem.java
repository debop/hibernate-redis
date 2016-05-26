package org.hibernate.examples.mapping.associations.onetomany.set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * org.hibernate.examples.mapping.associations.onetomany.set.OneToManyBiddingItem
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 2:06
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToManyBiddingItem extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;

  private String description;

  @OneToMany(mappedBy = "item", cascade = {CascadeType.ALL}, orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.EXTRA)
  private Set<OneToManyBid> bids = new HashSet<OneToManyBid>();

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name)
        .add("description", description);
  }

  private static final long serialVersionUID = -5361026108113198323L;
}
