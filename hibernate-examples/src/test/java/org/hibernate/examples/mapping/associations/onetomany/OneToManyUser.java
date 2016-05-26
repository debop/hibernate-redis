package org.hibernate.examples.mapping.associations.onetomany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * org.hibernate.examples.mapping.associations.onetomany.OneToManyUser
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 1:04
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class OneToManyUser extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String city;

  @OneToMany(cascade = {CascadeType.ALL})
  @JoinTable(name = "OneToOne_User_Address")
  @MapKeyColumn(name = "nick")
  @LazyCollection(LazyCollectionOption.EXTRA)
  @Fetch(FetchMode.SUBSELECT)
  private Map<String, OneToManyAddress> addresses = new HashMap<String, OneToManyAddress>();

  @ElementCollection
  @JoinTable(name = "OneToMany_Nicks", joinColumns = {@JoinColumn(name = "userId")})
  @Cascade({org.hibernate.annotations.CascadeType.ALL})
  private Set<String> nicknames = new HashSet<String>();


  @Override
  public int hashCode() {
    return HashTool.compute(city);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("city", city);
  }

  private static final long serialVersionUID = -1397252295104008999L;
}
