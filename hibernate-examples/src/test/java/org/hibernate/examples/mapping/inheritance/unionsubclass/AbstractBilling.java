package org.hibernate.examples.mapping.inheritance.unionsubclass;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;

/**
 * 상속관계의 엔테티들을 독립적인 테이블로 만든다.
 * 주의할 점은 Identifier 는 상속된 모든 class에 대해 고유한 값을 가져야 한다. (테이블 범위의 identity는 사용하면 안된다)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오전 12:31
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
// 여러 테이블에 걸쳐 Identity를 유지하기 위해 Sequence 를 제작 (HSql, PostgreSql 에서만 지원)
// @SequenceGenerator(name = "UnionSubclassBilling_Seq", sequenceName = "UnionSubclassBilling_Seq")
public abstract class AbstractBilling extends AbstractHibernateEntity<String> {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Setter(AccessLevel.PROTECTED)
  private String id;

  @Column(name = "owner", nullable = false)
  private String owner;

  @Override
  public int hashCode() {
    return HashTool.compute(owner);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("owner", owner);
  }

  private static final long serialVersionUID = 90233307681044105L;
}
