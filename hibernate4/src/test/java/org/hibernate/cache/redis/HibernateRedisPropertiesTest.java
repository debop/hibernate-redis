package org.hibernate.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class HibernateRedisPropertiesTest {

  private static final String CACHE_PROPERTIES_PATH = "conf/hibernate-redis.properties";

  @Test
  public void loadProperties() throws Exception {
    Properties props = new Properties();

    log.debug("load hibernate-redis properties. path={}", CACHE_PROPERTIES_PATH);

    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(CACHE_PROPERTIES_PATH);
    assertThat(is).isNotNull();

    props.load(is);

    log.debug("properties... \n{}", props.toString());
  }
}
