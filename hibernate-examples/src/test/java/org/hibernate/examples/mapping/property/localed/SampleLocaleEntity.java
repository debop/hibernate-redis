package org.hibernate.examples.mapping.property.localed;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.examples.model.AbstractLocaleHibernateEntity;
import org.hibernate.examples.utils.HashTool;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 다국어 지원을 위해, Locale 별로 제목, 설명 속성을 관리하는 엔티티입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 4:16
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class SampleLocaleEntity extends AbstractLocaleHibernateEntity<Long, SampleLocaleValue> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String title;
  private String description;

  /**
   * 지역화 정보를 담당합니다.
   */
  @CollectionTable(name = "SampleLocaleEntityLocale", joinColumns = {@JoinColumn(name = "SampleLocaleEntityId")})
  @MapKeyClass(Locale.class)
  @ElementCollection(targetClass = SampleLocaleValue.class, fetch = FetchType.EAGER)
  @Cascade({org.hibernate.annotations.CascadeType.ALL})
  @LazyCollection(LazyCollectionOption.EXTRA)
  private Map<Locale, SampleLocaleValue> localeMap = new HashMap<Locale, SampleLocaleValue>();

  @Override
  public Map<Locale, SampleLocaleValue> getLocaleMap() {
    return localeMap;
  }

  /**
   * Java에서는 실행 시 Generic 수형을 없애버립니다.
   * scala나 c#은 generic으로 인스턴스를 생성할 수 있지만, Java는 불가능합니다.
   * 그래서 이 값을 꼭 구현해 주셔야 합니다.
   *
   * @return TLocalVal 인스턴스
   */
  @Override
  public SampleLocaleValue createDefaultLocaleValue() {
    return new SampleLocaleValue(title, description);
  }

  @Override
  public int hashCode() {
    return HashTool.compute(title);
  }


  private static final long serialVersionUID = -5886227684015321943L;
}
