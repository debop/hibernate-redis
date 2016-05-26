package org.hibernate.examples.mapping.embeddable;

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
 * org.hibernate.examples.mapping.embeddable.User
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 30. 오후 12:33
 */
@Entity
@Table(name = "Embeddable_User", indexes = {@Index(name = "ix_embeddable_user_username", columnList = "username, password"),
    @Index(name = "ix_user_email", columnList = "UserEmail")})
// index 에 순서를 주려면 hibernate의 Table annotation을 이용해야 합니다.
@org.hibernate.annotations.Table(appliesTo = "Embeddable_User")
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
class User extends AbstractHibernateEntity<Long> {

  protected User() {
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String firstname;

  private String lastname;

  @Column(length = 128, nullable = false)
  private String username;

  @Column(length = 64, nullable = false)
  private String password;

  @Column(name = "UserEmail")
  private String email;

  @Column(name = "IS_ACTIVE", nullable = false)
  private Boolean active = true;

  @Basic(fetch = FetchType.LAZY)
  private String exAttrs;

  @Embedded
  @AttributeOverrides(
      {
          @AttributeOverride(name = "street", column = @Column(name = "HomeStreet", length = 128)),
          @AttributeOverride(name = "zipcode", column = @Column(name = "HomeZipCode", length = 24)),
          @AttributeOverride(name = "city", column = @Column(name = "HomeCity", length = 128))
      }
  )
  private Address homeAddress = new Address();

  @Embedded
  @AttributeOverrides(
      {
          @AttributeOverride(name = "street", column = @Column(name = "OfficeStreet", length = 128)),
          @AttributeOverride(name = "zipcode", column = @Column(name = "OfficeZipCode", length = 24)),
          @AttributeOverride(name = "city", column = @Column(name = "OfficeCity", length = 128))
      }
  )
  private Address officeAddress = new Address();

  @Override
  public int hashCode() {
    return HashTool.compute(username, password);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("firstname", firstname)
        .add("lastname", lastname)
        .add("username", username)
        .add("userpwd", password)
        .add("userEmail", email)
        .add("homeAddress", homeAddress)
        .add("officeAddress", officeAddress);
  }

  private static final long serialVersionUID = -5638782007660883773L;
}
