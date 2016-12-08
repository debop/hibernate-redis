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

  }

  @Test
  public void testGetExpiryInSecondsWithPassedProperties() {

    Properties props = new Properties();
    props.setProperty("hibernate.cache.provider_configuration_file_resource_path", "");
    props.setProperty("redis.expiryInSeconds.default", "240");
    props.setProperty("redis.expiryInSeconds.hibernate.common", "0");
    props.setProperty("redis.expiryInSeconds.hibernate.account", "2400");

    RedisCacheUtil.loadCacheProperties(props);

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
