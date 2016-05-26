package org.hibernate.examples.hibernate.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.hibernate.examples.model.UpdatedTimestampEntity;

import java.util.Iterator;

/**
 * 엔티티의 최신 갱신 시각을 관리하는 Interceptor 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:50
 */
@Slf4j
public class UpdatedTimestampInterceptor extends EmptyInterceptor {

  /**
   * 엔티티 저장 전에 최신 갱신 시각을 새롭게 갱신합니다.
   */
  @Override
  public void preFlush(Iterator entities) {
    while (entities.hasNext()) {
      Object entity = entities.next();
      if (entity instanceof UpdatedTimestampEntity) {
        ((UpdatedTimestampEntity) entity).updateUpdatedTimestamp();
      }
    }
  }


  private static final long serialVersionUID = -1965299893946137808L;
}
