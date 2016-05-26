package org.hibernate.examples.mapping.property.enumerated;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;

import javax.persistence.*;

/**
 * Enum 값 하나만을 표현하는 예제.
 * 복수의 Enum 값을 가지는 경우 C#에서는 @flags 를 쓰지만, java에서는 EnumSet을 사용해야 합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 1:56
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class EnumeratedEntity extends AbstractHibernateEntity<Integer> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.PROTECTED)
  private Integer id;

  // Enumerated 를 쓰면 Enum 값을 원하는 Ordinal 수형으로 DB에 저장하고, 반환받을 수 있다. 대부분 String 을 사용한다.
  @Enumerated(EnumType.ORDINAL)
  @Column(name = "OrdinalValue", nullable = false)
  private OrdinalEnum intValue = OrdinalEnum.Third;

  // Enumerated 를 쓰면 Enum 값을 원하는 수형으로 DB에 저장하고, 반환받을 수 있다. 대부분 String 을 사용한다.
  @Enumerated(EnumType.STRING)
  @Column(name = "StringValue", nullable = false)
  private StringEnum stringValue = StringEnum.Decimal;

  @Override
  public int hashCode() {
    return HashTool.compute(intValue, stringValue);
  }

  private static final long serialVersionUID = 4071809720463913052L;
}
