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

import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.File;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author debop sunghyouk.bae@gmail.com
 */
public class RedissonConfigurationTest {

  @Test
  public void loadFromResources() throws Exception {

    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/redisson.yaml");
    Config config = Config.fromYAML(is);
    assertThat(config).isNotNull();

    RedissonClient redisson = Redisson.create(config);
    testConnection(redisson);
  }

  @Test
  public void loadFromLocalFile() throws Exception {
    String path = "conf/redisson.yaml";
    File file = new File(path);
    Config config = Config.fromYAML(file);

    RedissonClient redisson = Redisson.create(config);
    testConnection(redisson);

  }

  private void testConnection(RedissonClient redisson) {
    assertThat(redisson).isNotNull();
    RBucket<String> bucket = redisson.getBucket("test");
    assertThat(bucket).isNotNull();
    bucket.set("value");
    assertThat(bucket.isExists()).isTrue();
    assertThat(bucket.delete()).isTrue();
  }
}
