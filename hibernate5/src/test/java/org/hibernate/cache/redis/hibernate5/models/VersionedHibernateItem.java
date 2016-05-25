package org.hibernate.cache.redis.hibernate5.models;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cache.redis.AbstractHibernateEntity;

import javax.persistence.*;

@Entity
@Cache(region = "redis:common:versioned", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class VersionedHibernateItem extends AbstractHibernateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Version
  private Long version;

  private String name;
  private String description;

  @Override
  public int hashCode() {
    return Objects.hashCode(name);
  }

  private static final long serialVersionUID = -4493988598547657663L;
}
