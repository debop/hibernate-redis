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
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate5.common")).isEqualTo(0);
    assertThat(RedisCacheUtil.getExpiryInSeconds("hibernate5.account")).isEqualTo(1200);
  }
}
