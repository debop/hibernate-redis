package org.hibernate.examples.hibernate.listener;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.examples.model.UpdatedTimestampEntity;

/**
 * org.hibernate.examples.hibernate.listener.UpdatedTimestampEntityListener
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:56
 */
public class UpdatedTimestampEntityListener implements PreInsertEventListener, PreUpdateEventListener {

  @Override
  public boolean onPreInsert(PreInsertEvent event) {
    if (event.getEntity() instanceof UpdatedTimestampEntity) {
      ((UpdatedTimestampEntity) event.getEntity()).updateUpdatedTimestamp();
    }
    return true;
  }

  @Override
  public boolean onPreUpdate(PreUpdateEvent event) {
    if (event.getEntity() instanceof UpdatedTimestampEntity) {
      ((UpdatedTimestampEntity) event.getEntity()).updateUpdatedTimestamp();
    }
    return true;
  }

  private static final long serialVersionUID = 1089954764026831038L;
}
