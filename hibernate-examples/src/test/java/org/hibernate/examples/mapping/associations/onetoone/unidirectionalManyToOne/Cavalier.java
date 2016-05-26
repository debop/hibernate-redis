package org.hibernate.examples.mapping.associations.onetoone.unidirectionalManyToOne;

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
 * Cavalier가 Horse와 1:1 매핑인데, 두 엔티티는 서로 독립적인 Id를 가집니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 2:45
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Cavalier extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "horseId")
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String name;

  /**
   * FetchType.LAZY 라면 outer join 이 아니라 따로 로드된다.
   */
  @OneToOne // (fetch = FetchType.LAZY)
  @JoinColumn(name = "horseId")
  // @LazyToOne(LazyToOneOption.PROXY)
  private Horse horse;

  @Override
  public int hashCode() {
    return HashTool.compute(name);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("name", name);
  }

  private static final long serialVersionUID = 7850333928981763050L;
}
