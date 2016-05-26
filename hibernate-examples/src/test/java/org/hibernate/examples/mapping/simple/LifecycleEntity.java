package org.hibernate.examples.mapping.simple;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;

import javax.persistence.*;
import java.util.Date;

/**
 * org.hibernate.examples.mapping.simple.LifecycleEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:59
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class LifecycleEntity extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;

  @org.hibernate.annotations.Generated(GenerationTime.INSERT)
  @Column(insertable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;


  @org.hibernate.annotations.Generated(GenerationTime.INSERT)
  @Column(insertable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  private static final long serialVersionUID = 9019361741633267121L;
}
