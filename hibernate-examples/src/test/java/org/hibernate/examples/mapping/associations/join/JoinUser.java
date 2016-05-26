package org.hibernate.examples.mapping.associations.join;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * org.hibernate.examples.mapping.associations.join.JoinUser
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 11:17
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class JoinUser extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;

  // @OneToMany를 이용한 Mapping 은 Entity여야 합니다.
  // 1:N 테이블 매핑을 수행합니다.
  @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
  @JoinTable(name = "JoinUserAddressMap")
  @MapKeyColumn(name = "nick")
  @ElementCollection(targetClass = JoinAddressEntity.class, fetch = FetchType.LAZY)
  private Map<String, JoinAddressEntity> addresses = new HashMap<String, JoinAddressEntity>();

  // 1:N 테이블 매핑을 수행합니다. (단순 수형인 경우 간단하게 처리됩니다)
  @JoinTable(name = "JoinUserNicknameMap", joinColumns = @JoinColumn(name = "UserId"))
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
  private Set<String> nicknames = new HashSet<String>();

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name);
  }

  private static final long serialVersionUID = -1086694041889310074L;
}
