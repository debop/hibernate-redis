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

package org.hibernate.cache.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author sunghyouk.bae@gmail.com
 */
@Slf4j
public class RedisCacheUtilTest {

  @Test
  public void testGetExpiryInSeconds() throws Exception {

    Properties props = new Properties();
    props.setProperty("hibernate.cache.provider_configuration_file_resource_path",
                      "classpath:conf/hibernate-redis.properties");

    RedisCacheUtil.loadCacheProperties(props);

    int expire = RedisCacheUtil.getExpiryInSeconds("xx");
    log.debug("no defined region's expiry in seconds={}", expire);
    assertThat(expire).isEqualTo(RedisCacheUtil.getDefaultExpiryInSeconds());

    assertThat(RedisCacheUtil.getExpiryInSeconds("default")).isEqualTo(360);
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate.common")).isEqualTo(0);
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate.account")).isEqualTo(1200);
  }

  @Test
  public void testGetExpiryInSecondsWithPassedProperties() {

    Properties props = new Properties();
    props.setProperty("hibernate.cache.provider_configuration_file_resource_path", "");
    props.setProperty("redis.expiryInSeconds.default", "240");
    props.setProperty("redis.expiryInSeconds.hibernate.common", "0");
    props.setProperty("redis.expiryInSeconds.hibernate.account", "2400");

    RedisCacheUtil.loadCacheProperties(props);

    assertThat(RedisCacheUtil.getExpiryInSeconds("default")).isEqualTo(240);
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate.common")).isEqualTo(0);
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate.account")).isEqualTo(2400);
  }

  @Test
  public void testGetExpiryInSecondsWithPassedPropertiesWithoutProviderConfigurationFileResourcePath() {

    Properties props = new Properties();
    //the following line is used to simulate there is no configuration on props and there is no default file on "conf/hibernate-redis.properties".
    //after that we will get is = null on RedisCacheUtil.
    props.setProperty("hibernate.cache.provider_configuration_file_resource_path", "-");
    props.setProperty("redis.expiryInSeconds.default", "240");
    props.setProperty("redis.expiryInSeconds.hibernate.common", "0");
    props.setProperty("redis.expiryInSeconds.hibernate.account", "2400");

    RedisCacheUtil.loadCacheProperties(props);

    //as real use case, don't call get default expiry
    //assertThat(RedisCacheUtil.getExpiryInSeconds("default")).isEqualTo(240);
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate.common")).isEqualTo(0);
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate.account")).isEqualTo(2400);
    //try to load the setting from default
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate.not_defined")).isEqualTo(240);
  }

  @Test
  public void testGetRedissonConfig() {

    Properties props = new Properties();
    RedisCacheUtil.loadCacheProperties(props);

    assertThat(RedisCacheUtil.getRedissonConfigPath()).isEqualTo("classpath:conf/redisson.yaml");
  }

  @Test
  public void testGetRedissonConfigPathWithPassedProperty() {

    Properties props = new Properties();
    props.setProperty("hibernate.cache.provider_configuration_file_resource_path", "");
    props.setProperty("redisson-config", "classpath:config/redisson.yaml");

    RedisCacheUtil.loadCacheProperties(props);

    assertThat(RedisCacheUtil.getRedissonConfigPath()).isEqualTo("classpath:config/redisson.yaml");
  }

}
