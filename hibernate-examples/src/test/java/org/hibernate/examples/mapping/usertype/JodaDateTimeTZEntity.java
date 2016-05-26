package org.hibernate.examples.mapping.usertype;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * org.hibernate.examples.mapping.usertype.JodaDateTimeTZEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 9. 오전 9:40
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class JodaDateTimeTZEntity extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue
  private Long id;

  /**
   * UTC DateTime 과 TimeZone 으로 분리해서 저장하고, 로드 시에는 통합합니다.
   */
  @Columns(columns = {@Column(name = "startTime"), @Column(name = "startTimeZone")})
  @Type(type = "org.hibernate.examples.usertype.JodaDateTimeTZUserType")
  private DateTime startTZ;

  @Columns(columns = {@Column(name = "endTime"), @Column(name = "endTimeZone")})
  @Type(type = "org.hibernate.examples.usertype.JodaDateTimeTZUserType")
  private DateTime endTZ;


  @Override
  public int hashCode() {
    return HashTool.compute(startTZ, endTZ);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("startTZ", startTZ)
        .add("endTZ", endTZ);
  }

  private static final long serialVersionUID = 5531635835898743185L;
}
