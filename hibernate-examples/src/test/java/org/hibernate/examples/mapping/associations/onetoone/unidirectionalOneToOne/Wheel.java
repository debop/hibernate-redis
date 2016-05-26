package org.hibernate.examples.mapping.associations.onetoone.unidirectionalOneToOne;

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

/**
 * org.hibernate.examples.mapping.associations.onetoone.unidirectionalOneToOne.Wheel
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 3:36
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Wheel extends AbstractHibernateEntity<Long> {

  @Id
  @Column(name = "vehicleId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;

  private double diameter;

  @MapsId
  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  // @PrimaryKeyJoinColumn
  @JoinColumn(name = "vehicleId")
  private Vehicle vehicle;

  @Override
  public int hashCode() {
    return HashTool.compute(name, diameter);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
                .add("name", name)
                .add("diameter", diameter);
  }

  private static final long serialVersionUID = -6537387709923172615L;
}
