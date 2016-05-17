package org.hibernate.examples.model;

import java.util.Set;

/**
 * 트리 구조를 가지는 엔티티를 표현하는 인터페이스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 4:29
 */
public interface HibernateTreeEntity<T extends HibernateTreeEntity<T>> extends PersistentObject {

  /**
   * 현재 노드의 부모 노드를 반환합니다. null이면, 현 노드가 최상위 노드 (root node) 입니다.
   */
  T getParent();

  /**
   * 부모 노드를 설정합니다. (null 을 설정하면, 최상위 노드(root node) 가 됩니다.)
   */
  void setParent(T parent);

  /**
   * 자식 노드 집합 (Set)
   */
  Set<T> getChildren();

  /**
   * 현재 노드의 트리 구조상의 위치를 나타냅니다.
   */
  TreeNodePosition getNodePosition();

  /**
   * 자식 노드를 추가합니다.
   */
  void addChild(T child);

  /**
   * 자식 노드를 삭제합니다.
   */
  void removeChild(T child);
}
