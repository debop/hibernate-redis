/*
 * Copyright (c) 2016. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.cache.redis.hibernate52.util;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Hibernate 2nd cache Utility class
 *
 * @author sunghyouk.bae@gmail.com
 */
public final class HibernateCacheUtil {

  private HibernateCacheUtil() {
  }

  public static String getRegionName(SessionFactory sessionFactory,
                                     Class entityClass) {
    EntityPersister p = ((SessionFactoryImpl) sessionFactory).getEntityPersister(entityClass.getName());

    if (p.hasCache()) {
      return p.getCacheAccessStrategy().getRegion().getName();
    }
    return "";
  }
}
