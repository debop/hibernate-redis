package org.hibernate.cache.redis.hibernate4.models;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cache.redis.AbstractHibernateEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@org.hibernate.annotations.Cache(region = "redis:common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class HibernateItem extends AbstractHibernateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;
  private String description;


  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  private static final long serialVersionUID = -4218175469758887306L;
}
