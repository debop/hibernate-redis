package org.hibernate.examples.mapping.associations.manytoone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;

/**
 * org.hibernate.examples.mapping.associations.manytoone.Beer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오전 9:37
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Beer extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;
  private Double price = 0.0;

  /**
   * Beer가 저장, 머지, 갱신될 때 Brewery 정보도 갱신하도록 합니다. (이런 경우는 잘 쓰지 않는다)
   */
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @LazyToOne(LazyToOneOption.PROXY)
  @JoinColumn(name = "breweryId", nullable = false)
  private Brewery brewery;

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name)
        .add("price", price);
  }

  private static final long serialVersionUID = 1335724074913569654L;
}
