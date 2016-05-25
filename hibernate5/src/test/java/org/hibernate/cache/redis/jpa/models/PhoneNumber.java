package org.hibernate.cache.redis.jpa.models;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author sunghyouk.bae@gmail.com
 */
@Getter
@Setter
public class PhoneNumber implements Serializable {

  private long personId = 0;
  private String numberType = "home";
  private long phone = 0;


  public boolean equals(Object obj) {
    if ((obj != null) && (obj instanceof PhoneNumber))
      return hashCode() == obj.hashCode();
    return false;
  }

  public int hashCode() {
    return Objects.hashCode(numberType, personId, phone);
  }

  public String toString() {
    return numberType + ":" + phone;
  }

  private static final long serialVersionUID = 6479899943706448748L;
}
