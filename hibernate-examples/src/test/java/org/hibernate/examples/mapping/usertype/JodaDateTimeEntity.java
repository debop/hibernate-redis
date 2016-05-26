package org.hibernate.examples.mapping.usertype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.model.DateTimeRange;
import org.hibernate.examples.utils.HashTool;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * org.hibernate.examples.mapping.usertype.JodaDateTimeEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 9:08
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class JodaDateTimeEntity extends AbstractHibernateEntity<Long> {

  @Id
  @GeneratedValue
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  @Column(name = "JodaStart")
  @Type(type = "org.hibernate.examples.usertype.JodaDateTimeUserType")
  private DateTime start;

  @Column(name = "JodaEnd")
  @Type(type = "org.hibernate.examples.usertype.JodaDateTimeUserType")
  private DateTime end;


  // 복합 수형인 경우 컬럼들을 명시해줘야 합니다.
  //
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "startTime", column = @Column(name = "rangeStart1")),
      @AttributeOverride(name = "endTime", column = @Column(name = "rangeEnd1"))
  })
  private DateTimeRange range1;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "startTime", column = @Column(name = "rangeStart2")),
      @AttributeOverride(name = "endTime", column = @Column(name = "rangeEnd2"))
  })
  private DateTimeRange range2;

  @Override
  public int hashCode() {
    return HashTool.compute(start, end, range1, range2);
  }

  private static final long serialVersionUID = -5195581340713775796L;
}
