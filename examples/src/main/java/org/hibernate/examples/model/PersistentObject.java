package org.hibernate.examples.model;

/**
 * 영구 저장된 객체인지를 표현하는 Interface입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 2:37
 */
public interface PersistentObject extends ValueObject {

  boolean isPersisted();

  /**
   * Object 가 저장되었을 때 #isPersisted() 를 true로 갱신한다.
   */
  void onSave();

  /**
   * Object 가 영구저장소에서 로드될 때 #isPersisted() 를 true 로 갱신한다.
   */
  void onLoad();
}
