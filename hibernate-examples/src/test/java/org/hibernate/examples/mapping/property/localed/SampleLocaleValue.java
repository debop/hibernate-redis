package org.hibernate.examples.mapping.property.localed;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.examples.model.AbstractValueObject;
import org.hibernate.examples.model.LocaleValue;
import org.hibernate.examples.utils.HashTool;

import javax.persistence.Embeddable;

/**
 * 제목,설명 속성이 지역화 정보로 지정된 예
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 4:14
 */
@Embeddable
@Getter
@Setter
public class SampleLocaleValue extends AbstractValueObject implements LocaleValue {

  public SampleLocaleValue() {
  }

  public SampleLocaleValue(String title, String description) {
    this.title = title;
    this.description = description;
  }

  private String title;

  private String description;

  @Override
  public int hashCode() {
    return HashTool.compute(title);
  }

  private static final long serialVersionUID = -124348975681798754L;
}
