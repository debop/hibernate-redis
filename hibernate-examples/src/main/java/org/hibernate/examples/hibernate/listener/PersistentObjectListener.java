package org.hibernate.examples.hibernate.listener;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.examples.model.PersistentObject;
import org.hibernate.persister.entity.EntityPersister;

/**
 * org.hibernate.examples.hibernate.listener.PersistentObjectListener
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:55
 */
public class PersistentObjectListener implements PostLoadEventListener, PostInsertEventListener {

  @Override
  public void onPostInsert(PostInsertEvent event) {
    if (event.getEntity() instanceof PersistentObject) {
      ((PersistentObject) event.getEntity()).onSave();
    }
  }

  // @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    return false;
  }

  @Override
  public void onPostLoad(PostLoadEvent event) {
    if (event.getEntity() instanceof PersistentObject) {
      ((PersistentObject) event.getEntity()).onLoad();
    }
  }

  private static final long serialVersionUID = -2581284706244288775L;
}
