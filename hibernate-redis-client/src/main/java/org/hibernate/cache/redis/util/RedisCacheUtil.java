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

import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.client.RedisClient;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Hibernate Redis Cache Utility Class
 *
 * @author debop sunghyouk.bae@gmail.com
 */
@Slf4j
public final class RedisCacheUtil {

  public static final String FILE_URL_PREFIX = "file:";
  public static final String RESOURCE_URL_PREFIX = "classpath:";
  public static final String EXPIRY_PROPERTY_PREFIX = "redis.expiryInSeconds";
  public static final int DEFAULT_EXPIRY_IN_SECONDS = 120;

  public static final String REDISSON_CONFIG = "redisson-config";
  public static final String REDISSON_JAVA_CONFIG = "redisson-java-config";
  public static final String DEFAULT_REDISSON_CONFIG_PATH = "classpath:conf/redisson.yaml";
  public static final String REDIS_SINGLETON_CLIENT = "hibernate-redis-singleton-client";

  private static final Properties cacheProperties = new Properties();

  private static int defaultExpiryInSeconds = DEFAULT_EXPIRY_IN_SECONDS;

  private RedisCacheUtil() { }

  public static int getDefaultExpiryInSeconds() {
    return defaultExpiryInSeconds;
  }

  private static AtomicReference<RedissonClient> redissonClient = null;

  /**
   * Load hibernate-redis.properties
   *
   * @param props hibernate configuration
   * @return hibernate-redis configuration
   */
  public static Properties loadCacheProperties(final Properties props) {
    cacheProperties.putAll(props);

    Config config = (Config) props.get(REDISSON_JAVA_CONFIG);
    if (config != null) {
      log.debug("Loading redisson config from Java Object in system properties");
      cacheProperties.put(REDISSON_JAVA_CONFIG, config);
    }
      String cachePropsPath = props.getProperty("hibernate.cache.provider_configuration_file_resource_path",
              RESOURCE_URL_PREFIX + "conf/hibernate-redis.properties");
      InputStream is = null;
      try {
        log.debug("Loading cache properties... path={}", cachePropsPath);
        is = getFileInputStream(cachePropsPath);
        cacheProperties.load(is);

      } catch (Exception e) {
        log.warn("Fail to load cache properties. path={}", cachePropsPath, e);
      } finally {
        loadDefaultExpiry();
        if (is != null) {
          try {
            is.close();
          } catch (Exception ignored) {
          }
        }
      }
    return cacheProperties;
  }

  /**
   * Open outer file or resource file
   *
   * @param path file path
   * @return Input stream
   * @throws IOException
   */
  public static InputStream getFileInputStream(String path) throws IOException {
    InputStream is = null;
    if (path.startsWith(FILE_URL_PREFIX)) {
      String filepath = path.substring(FILE_URL_PREFIX.length());
      is = new FileInputStream(new File(filepath));
    } else if (path.startsWith(RESOURCE_URL_PREFIX)) {
      is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.substring(RESOURCE_URL_PREFIX.length()));
    } else {
      // load from resources
      is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }
    return is;
  }

  /**
   * Load default expiry (seconds)
   */
  private static void loadDefaultExpiry() {
    try {
      defaultExpiryInSeconds = Integer.parseInt(getProperty(EXPIRY_PROPERTY_PREFIX + ".default", String.valueOf(DEFAULT_EXPIRY_IN_SECONDS)));
    } catch (Exception ignored) {
      defaultExpiryInSeconds = DEFAULT_EXPIRY_IN_SECONDS;
    }
  }

  /**
   * Get Redisson configuration file path (redisson.yaml)
   *
   * @return Redisson configuration yaml file path
   */
  public static String getRedissonConfigPath() {
    return cacheProperties.getProperty(REDISSON_CONFIG, DEFAULT_REDISSON_CONFIG_PATH);
  }


  /**
   * get expiry timeout (seconds) by region name.
   *
   * @param region region name
   * @return expiry (seconds)
   */
  public static int getExpiryInSeconds(final String region) {
    try {
      String key = EXPIRY_PROPERTY_PREFIX + "." + region;
      String value = getProperty(key, String.valueOf(defaultExpiryInSeconds));

      log.trace("load expiry property. region={}, expiryInSeconds={}", key, value);

      return Integer.parseInt(value);
    } catch (Exception e) {
      log.warn("Fail to get expiryInSeconds in region={}", region, e);
      return defaultExpiryInSeconds;
    }
  }

  /**
   * get property value
   *
   * @param key          property key
   * @param defaultValue default value for not found key
   * @return property value
   */
  public static String getProperty(final String key, String defaultValue) {
    if (cacheProperties == null || cacheProperties.isEmpty()) {
      return defaultValue;
    }
    try {
      String value = cacheProperties.getProperty(key, defaultValue);
      log.trace("get property. key={}, value={}, defaultValue={}", key, value, defaultValue);
      return value;
    } catch (Exception ignored) {
      log.warn("error occurred in reading properties. key={}", key, ignored);
      return defaultValue;
    }
  }

  /**
   * get property value of REDISSION_JAVA_CONFIG
   * @return property value
   */
  public static Config getRedissonJavaConfig() {
    return (Config) cacheProperties.get(REDISSON_JAVA_CONFIG);
  }

  public static void saveRedisSingletonClient(RedisClient client) {
    cacheProperties.put(REDIS_SINGLETON_CLIENT, client);
  }

  public static RedisClient getRedisSingletonClient() {
    return (RedisClient) cacheProperties.getOrDefault(REDIS_SINGLETON_CLIENT, null);
  }

}
