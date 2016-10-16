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

package org.hibernate.cache.redis.config;

import lombok.Getter;
import lombok.Setter;
import org.redisson.config.Config;

/**
 * SingleRedissonConfigFactory
 *
 * @author sunghyouk.bae@gmail.com
 */
@Getter
@Setter
public class SingleRedissonConfigFactory implements RedissonConfigFactory {

  public SingleRedissonConfigFactory() {
    this("localhost", 6379, 0);
  }

  public SingleRedissonConfigFactory(String host, int port, int database) {
    this.host = host;
    this.port = port;
    this.database = database;
  }

  private final String host;
  private final int port;
  private final int database;


  @Override
  public Config create() {
    Config cfg = new Config();
    cfg.useSingleServer()
       .setAddress(host + ":" + port)
       .setDatabase(database);

    return cfg;
  }
}
