package org.hibernate.examples.hibernate.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.hibernate.examples.model.PersistentObject;

import java.io.Serializable;

/**
 * Hibernate PersistentObject의 영구저장 여부를 관리할 수 있도록 해주는 Interceptor입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 11:06
 */
@Slf4j
public class PersistentObjectInterceptor extends EmptyInterceptor {

  public boolean isPersisted(Object entity) {
    return entity instanceof PersistentObject && ((PersistentObject) entity).isPersisted();
  }

  /**
   * 엔티티 로드 후 {@link org.hibernate.examples.model.PersistentObject#onLoad()} 를 호출합니다.
   */
  @Override
  public boolean onLoad(Object entity,
                        Serializable id,
                        Object[] state,
                        String[] propertyNames,
                        org.hibernate.type.Type[] types) {
    if (entity instanceof PersistentObject) {
      ((PersistentObject) entity).onLoad();
    }
    return false;
  }

  /**
   * 엔티티 저장 후 {@link org.hibernate.examples.model.PersistentObject#onSave()}를 호출합니다.
   */
  @Override
  public boolean onSave(Object entity,
                        Serializable id,
                        Object[] state,
                        String[] propertyNames,
                        org.hibernate.type.Type[] types) {
    if (entity instanceof PersistentObject) {
      ((PersistentObject) entity).onSave();
    }
    return false;
  }

  private static final long serialVersionUID = -9072457259066614636L;
}
