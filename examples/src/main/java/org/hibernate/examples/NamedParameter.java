package org.hibernate.examples;

import java.io.Serializable;

/**
 * 쿼리 {@link org.hibernate.Query} 등에 사용할 Parameter의 기본 interface
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:22
 */
public interface NamedParameter extends Serializable {

  /**
   * 파라미터 명
   */
  String getName();

  /**
   * 파라미터 값 반환
   */
  Object getValue();

  /**
   * 파라미터 값 지정
   *
   * @param v 파리미터 값
   */
  void setValue(Object v);
}
