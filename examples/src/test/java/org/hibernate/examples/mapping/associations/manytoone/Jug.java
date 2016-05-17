package org.hibernate.examples.mapping.associations.manytoone;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * org.hibernate.examples.mapping.associations.manytoone.Jug
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오전 10:17
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Jug extends AbstractHibernateEntity<Long> {

  public Jug() {
  }

  public Jug(String name) {
    this.name = name;
  }

  @Id
  @GeneratedValue
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  @Column(length = 100)
  private String name;

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper().add("name", name);
  }

  private static final long serialVersionUID = -2644369668002026174L;
}
