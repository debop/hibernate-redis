package org.hibernate.examples.jpa.config;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;


@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQuery(name = "JpaAccount.findByName", query = "select ja from JpaAccount ja where ja.name=?1")
@Getter
@Setter
public class JpaAccount extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Double cashBalance;

  @Column(name = "AccountName", nullable = false, length = 32)
  private String name;

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name);
  }

  private static final long serialVersionUID = 8986275418970766284L;
}
