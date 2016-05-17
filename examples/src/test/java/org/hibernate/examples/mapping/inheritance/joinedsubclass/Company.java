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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * org.hibernate.examples.mapping.inheritance.joinedsubclass.Company
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 30. 오후 12:58
 */
@Entity(name = "JoinedSubclass_Company")
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Company extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  public String name;

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name);
  }

  private static final long serialVersionUID = 7470890978065566926L;
}
