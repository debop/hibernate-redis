package org.hibernate.examples.utils;

/**
 * org.hibernate.examples.utils.HashTool
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 2:48
 */
public final class HashTool {

  private HashTool() {
  }

  /**
   * The constant NULL_VALUE.
   */
  public static final int NULL_VALUE = 0;
  /**
   * The constant ONE_VALUE.
   */
  public static final int ONE_VALUE = 1;
  /**
   * The constant FACTOR.
   */
  public static final int FACTOR = 31;

  /**
   * 해시코드를 생성합니다.
   *
   * @param x 해시코드를 생성할 객체
   * @return 해시코드
   */
  private static int computeInternal(final Object x) {
    return (x != null) ? x.hashCode() : NULL_VALUE;
  }

  /**
   * 지정된 객체들의 Hash Code를 조합한 Hash Code를 생성합니다.
   *
   * @param objs 해쉬코드를 생성할 객체 배열
   * @return 조합된 Hash code
   */
  public static int compute(final Object... objs) {
    if (objs == null || objs.length == 0)
      return NULL_VALUE;

    int hash = NULL_VALUE;
    for (Object x : objs) {
      hash = hash * FACTOR + computeInternal(x);
    }
    return hash;
  }
}
