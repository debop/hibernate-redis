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
