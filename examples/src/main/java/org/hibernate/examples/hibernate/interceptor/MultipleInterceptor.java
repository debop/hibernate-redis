package org.hibernate.examples.hibernate.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.*;

/**
 * Hibernate에 등록할 수 있는 Interceptor는 하나입니다.
 * MultipleInterceptor는 복수의 Interceptor이 작업할 수 있도록 해줍니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:51
 */
@Slf4j
public class MultipleInterceptor extends EmptyInterceptor {

  private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

  public MultipleInterceptor(Collection<? extends Interceptor> c) {
    this.interceptors.addAll(c);
  }

  public MultipleInterceptor(Interceptor... array) {
    Collections.addAll(this.interceptors, array);
  }

  public void addInterceptor(Interceptor interceptor) {
    this.interceptors.add(interceptor);
  }

  public void removeInterceptor(Interceptor interceptor) {
    this.interceptors.remove(interceptor);
  }

  public boolean exists() {
    return interceptors.size() > 0;
  }

  @Override
  public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    if (exists()) {
      for (Interceptor interceptor : interceptors) {
        interceptor.onDelete(entity, id, state, propertyNames, types);
      }
    }
  }

  @Override
  public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
    if (exists()) {
      for (Interceptor interceptor : interceptors) {
        interceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
      }
    }
    return false;
  }

  @Override
  public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    if (exists()) {
      for (Interceptor interceptor : interceptors) {
        interceptor.onSave(entity, id, state, propertyNames, types);
      }
    }
    return false;
  }

  @Override
  public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    if (exists()) {
      for (Interceptor interceptor : interceptors) {
        interceptor.onLoad(entity, id, state, propertyNames, types);
      }
    }
    return false;
  }

  @Override
  public void postFlush(Iterator entities) {
    if (exists()) {
      for (Interceptor interceptor : interceptors) {
        interceptor.postFlush(entities);
      }
    }
  }

  @Override
  public void preFlush(Iterator entities) {
    if (exists()) {
      for (Interceptor interceptor : interceptors) {
        interceptor.preFlush(entities);
      }
    }
  }

  private static final long serialVersionUID = -818808374889268894L;
}
