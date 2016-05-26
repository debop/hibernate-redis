package org.hibernate.examples.hibernate.config;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * 클래스, 속성명을 ORACLE 명명규칙을 사용하여 DB 엔티티의 요소를 변경한다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 9:49
 */
public class OracleNamingStrategy extends ImprovedNamingStrategy {

  /**
   * 지정된 클래스의 단어들을 underscore(_) 로 구분된 문자열로 변환합니다. (UserFigure => USER_FIGURE)
   *
   * @param className 클래스 명
   * @return Oracle 명명 규칙에 따른 테이블명
   */
  @Override
  public String classToTableName(String className) {
    return super.classToTableName(className).toUpperCase();
  }

  /**
   * 지정된 속성명의 단어들을 underscore(_) 로 구분된 문자열로 변환합니다. (UserId => USER_ID)
   *
   * @param propertyName 속성명
   * @return Oracle 명명 규칙에 따른 컬럼명
   */
  @Override
  public String propertyToColumnName(String propertyName) {
    return super.propertyToColumnName(propertyName).toUpperCase();
  }

  /**
   * 지정된 테이블명의 단어들을 underscore(_) 로 구분된 문자열로 변환합니다. (UserFigure => USER_FIGURE)
   *
   * @param tableName
   * @return
   */
  @Override
  public String tableName(String tableName) {
    return super.tableName(tableName).toUpperCase();
  }

  /**
   * 지정된 컬럼명의 단어들을 underscore(_) 로 구분된 문자열로 변환합니다. (UserId => USER_ID)
   *
   * @param columnName 컬럼명
   * @return Oracle 명명 규칙에 따른 컬럼명
   */
  @Override
  public String columnName(String columnName) {
    return super.columnName(columnName).toUpperCase();
  }

  private static final long serialVersionUID = -4830733993241581835L;
}
