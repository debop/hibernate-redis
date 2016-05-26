package org.hibernate.examples.model;

/**
 * Hibernate, JPA 용 엔티티의 기본 인터페이스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 2:36
 */
public interface HibernateEntity<TId> extends PersistentObject {

  /**
   * Entity의 Identifier 값을 반환합니다.
   *
   * @return Identifier value
   */
  TId getId();
}
