package org.hibernate.examples.model;

import org.hibernate.examples.utils.ToStringHelper;

/**
 * 영구 저장소에 저장 여부를 나타내는 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 2:37
 */
public abstract class AbstractPersistentObject extends AbstractValueObject implements PersistentObject {

  private boolean persisted;

  @Override
  public boolean isPersisted() {
    return persisted;
  }

  protected void setPersisted(Boolean v) {
    persisted = v;
  }

  @Override
  public void onSave() {
    setPersisted(true);
  }

  @Override
  public void onLoad() {
    setPersisted(true);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("persisted", persisted);
  }

  private static final long serialVersionUID = -1668910261730798160L;
}
