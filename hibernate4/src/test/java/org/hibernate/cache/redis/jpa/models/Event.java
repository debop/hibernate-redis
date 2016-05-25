package org.hibernate.cache.redis.jpa.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.cache.redis.AbstractHibernateEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author sunghyouk.bae@gmail.com
 */
@Entity
@Getter
@Setter
public class Event extends AbstractHibernateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;


  private String title;

  private static final long serialVersionUID = 8201868945321506870L;
}
