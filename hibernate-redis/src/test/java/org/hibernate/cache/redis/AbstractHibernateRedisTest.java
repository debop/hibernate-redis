/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis;

import lombok.SneakyThrows;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;


public abstract class AbstractHibernateRedisTest {

  public static final String REDISSON_CONFIG = "conf/redisson.yaml";

  @SneakyThrows
  public static RedissonClient createRedisson() {
    File file = new File(REDISSON_CONFIG);
    assertThat(file.exists()).isTrue();

    Config config = Config.fromYAML(file);
    return Redisson.create(config);
  }
}
