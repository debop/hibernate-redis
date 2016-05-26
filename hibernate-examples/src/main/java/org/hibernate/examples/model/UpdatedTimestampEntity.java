package org.hibernate.examples.model;

import org.joda.time.DateTime;

/**
 * 최근 수정일자를 속성으로 가지고 있는 엔티티를 표현하는 인터페이스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:13
 */
public interface UpdatedTimestampEntity {

  /**
   * 엔티티의 최근 갱신 일자를 반환합니다.
   */
  DateTime getUpdatedTimestamp();

  /**
   * 엔티티의 최근 갱신 일자를 수정합니다.
   */
  void updateUpdatedTimestamp();
}
