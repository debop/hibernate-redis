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

package org.hibernate.cache.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.util.RedisCacheUtil;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class HibernateRedisConfigTest {

  @Test
  public void loadFromPropertiesFile() throws Exception {
    String path = "file:conf/hibernate-redis.properties";
    InputStream is = RedisCacheUtil.getFileInputStream(path);
    assertThat(is).isNotNull();

    Properties props = new Properties();
    props.load(is);
    testProperties(props);
  }

  @Test
  public void loadFromPropertiesInResources() throws Exception {
    String path = "classpath:conf/hibernate-redis.properties";
    InputStream is = RedisCacheUtil.getFileInputStream(path);
    assertThat(is).isNotNull();

    Properties props = new Properties();
    props.load(is);
    testProperties(props);
  }

  private void testProperties(Properties props) throws Exception {
    assertThat(props).isNotNull();

    RedisCacheUtil.loadCacheProperties(props);
    log.debug("all properties: {}", props.toString());

    String redissonConf = props.getProperty("redisson-config");
    assertThat(redissonConf).isNotEmpty();

    // default expiry
    int defaultExpiry = RedisCacheUtil.getExpiryInSeconds("xx");
    log.debug("defaultExpiry={}", defaultExpiry);
    assertThat(defaultExpiry).isEqualTo(RedisCacheUtil.getDefaultExpiryInSeconds());
  }
}
