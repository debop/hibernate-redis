package org.hibernate.examples.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * org.hibernate.examples.model.DateTimeRange
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 9:10
 */
@Embeddable
@Getter
@Setter
public class DateTimeRange extends AbstractValueObject {

  private static final long ZeroMillis = 0L;
  private static final long MinMillis = 0L;
  private static final long MillisPerDay = 24 * 60 * 60 * 1000L;
  private static final long MaxMillis = 3652059L * MillisPerDay - 1L;

  private static final DateTime MinPeriodTime = new DateTime(MinMillis);
  private static final DateTime MaxPeriodTime = new DateTime(MaxMillis);

  public DateTimeRange() {
    this(MinPeriodTime, MaxPeriodTime);
  }

  public DateTimeRange(DateTime startTime, DateTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public DateTimeRange(DateTimeRange src) {
    this.startTime = src.startTime;
    this.endTime = src.endTime;
  }

  @Type(type = "org.hibernate.examples.usertype.JodaDateTimeUserType")
  @Column(name = "startTime")
  private DateTime startTime;

  @Type(type = "org.hibernate.examples.usertype.JodaDateTimeUserType")
  @Column(name = "endTime")
  private DateTime endTime;

  public void setRange(DateTime startTime, DateTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  @Override
  public int hashCode() {
    return HashTool.compute(startTime, endTime);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("startTime", startTime)
        .add("endTime", endTime);
  }

  private static final long serialVersionUID = -1025137254753678068L;
}
