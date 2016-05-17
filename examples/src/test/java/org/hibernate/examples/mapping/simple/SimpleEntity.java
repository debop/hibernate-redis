package org.hibernate.examples.mapping.simple;

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
 * org.hibernate.examples.mapping.simple.SimpleEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:57
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class SimpleEntity extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;

  private String description;

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

  private static final long serialVersionUID = 3250454084254905337L;
}
