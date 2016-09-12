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

package org.hibernate.cache.redis;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class AbstractHibernateEntity implements Serializable {

  abstract public Integer getId();


  @Override
  public boolean equals(Object obj) {
    if ((obj != null) && (obj.getClass() == this.getClass())) {
      Integer objId = ((AbstractHibernateEntity) obj).getId();

      int objHash = (objId != null) ? objId.hashCode() : obj.hashCode();
      int hash = (getId() != null) ? getId().hashCode() : hashCode();

      return objHash == hash;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (getId() != null) ? getId().hashCode() : super.hashCode();
  }

  private static final long serialVersionUID = -7079974059265743197L;
}
