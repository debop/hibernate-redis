package org.hibernate.cache.redis.hibernate4.util;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Hibernate 2nd cache Utility class
 *
 * @author sunghyouk.bae@gmail.com
 */
public final class HibernateCacheUtil {

  private HibernateCacheUtil() {}

  public static String getRegionName(SessionFactory sessionFactory,
                                     Class entityClass) {
    EntityPersister p = ((SessionFactoryImpl) sessionFactory).getEntityPersister(entityClass.getName());

    if (p.hasCache()) {
      return p.getCacheAccessStrategy().getRegion().getName();
    }
    return "";
  }
}
