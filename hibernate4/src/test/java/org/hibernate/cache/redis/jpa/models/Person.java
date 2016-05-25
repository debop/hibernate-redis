package org.hibernate.cache.redis.jpa.models;

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
@org.hibernate.annotations.Cache(region = "account", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Person extends AbstractHibernateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;


  private Integer age;
  private String firstname;
  private String lastname;

  private Float weight = 77.7F;
  private Double height = 188.8D;


  @Override
  public int hashCode() {
    return Objects.hashCode(firstname, lastname);
  }

  private static final long serialVersionUID = 5899077467654859662L;
}
