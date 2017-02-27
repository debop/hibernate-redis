/*
 * Copyright (c) 2017. Sunghyouk Bae <sunghyouk.bae@gmail.com>
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

package org.hibernate.stresser.persistence.dao;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.stresser.persistence.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
@Entity
@Table(name = "player")
@Cache(region = "Player", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Player extends AbstractEntity {

  private String name;
  private int[] array = new int[]{1024};
  private Date updateTime;
  private int count;

  public void setName(String name) {
    this.name = name;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public void setCount(int count) {
    this.count = count;
  }
}
