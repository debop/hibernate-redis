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
