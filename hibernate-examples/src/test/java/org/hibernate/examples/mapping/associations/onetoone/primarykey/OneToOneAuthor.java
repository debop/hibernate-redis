package org.hibernate.examples.mapping.associations.onetoone.primarykey;

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
 * org.hibernate.examples.mapping.associations.onetoone.primarykey.OneToOneAuthor
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 3:18
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToOneAuthor extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "authorId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;

  // FetchType.LAZY 이므로 따로 로드합니다.
  @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  @PrimaryKeyJoinColumn
  private OneToOneBiography biography = new OneToOneBiography(this);

  // FetchType.EAGER 이므로 left outer join 으로 로드합니다.
  @OneToOne(cascade = {CascadeType.ALL})
  @PrimaryKeyJoinColumn
  private OneToOnePicture picture = new OneToOnePicture(this);

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name);
  }

  private static final long serialVersionUID = 9107358535833367961L;
}
