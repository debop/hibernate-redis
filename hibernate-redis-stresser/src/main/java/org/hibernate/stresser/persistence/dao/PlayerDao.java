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

import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
@Repository
public class PlayerDao {

  @Autowired
  private SessionFactory sessionFactory;

  @Transactional(readOnly = true)
  public Player get(Serializable objectId) {
    return getSession().get(Player.class, objectId);
  }

  @Transactional
  public void saveAll(int amount) {
    for (int i = 0; i <= amount; i++) {
      Player player = new Player();
      player.setName("player" + i);
      player.setUpdateTime(new Date());
      getSession().save(player);
    }
  }

  @Transactional
  public void update(int playerId, int count) {
    Player player = getSession().load(Player.class, playerId, LockOptions.UPGRADE);
    player.setUpdateTime(new Date());
    player.setCount(count);
  }

  protected Session getSession() {
    return sessionFactory.getCurrentSession();
  }
}
