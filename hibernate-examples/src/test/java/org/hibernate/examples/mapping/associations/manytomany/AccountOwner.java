package org.hibernate.examples.mapping.associations.manytomany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 은행 계정 소유자
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 11:45
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class AccountOwner extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ownerId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  /**
   * 사회보장번호 (주민번호)
   */
  @Column(length = 32)
  private String SSN;

  /**
   * 사용자의 은행계좌 정보
   * many-to-many 에서는 둘 중 하나는 mappedBy 를 지정해야 한다.
   */
  @ManyToMany(cascade = {CascadeType.ALL})
  @JoinTable(name = "BankAccountOwners",
      joinColumns = {@JoinColumn(name = "ownerId")},
      inverseJoinColumns = {@JoinColumn(name = "accountId")})
  private Set<BankAccount> bankAccounts = new HashSet<BankAccount>();

  @Override
  public int hashCode() {
    return HashTool.compute(SSN);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("SSN", SSN);
  }

  private static final long serialVersionUID = 6041020627741330687L;
}
