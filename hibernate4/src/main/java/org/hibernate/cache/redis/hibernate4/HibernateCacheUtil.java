package org.hibernate.cache.redis.hibernate4;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Hibernate utility class
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 9. 26. 오후 4:49
 */
public final class HibernateCacheUtil {

  public static String getRegionName(SessionFactory sessionFactory, Class entityClass) {
    EntityPersister p = ((SessionFactoryImpl) sessionFactory).getEntityPersister(entityClass.getName());
    return p.hasCache()
        ? p.getCacheAccessStrategy().getRegion().getName()
        : "";
  }
}
