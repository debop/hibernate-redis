package org.hibernate.examples.mapping.inheritance.joinedsubclass;

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
 * org.hibernate.examples.mapping.inheritance.joinedsubclass.Person
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 30. 오후 12:54
 */
@Entity
@Table(name = "JoinedSubclass_Person")
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public abstract class Person extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  @Column(name = "personName", nullable = false, length = 128)
  private String name;

  @Column(name = "regidentNo", nullable = false, length = 128)
  private String regidentNo;

  private Integer age;

  @Override
  public int hashCode() {
    return HashTool.compute(name, regidentNo);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name)
        .add("regidentNo", regidentNo);
  }

  private static final long serialVersionUID = 823321933233116966L;
}
