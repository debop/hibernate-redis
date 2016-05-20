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

package org.hibernate.cache.redis.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

/**
 * @author debop sunghyouk.bae@gmail.com
 */
@Slf4j
public final class RedisCacheUtil {

  private static final String FILE_URL_PREFIX = "file:";
  private static final String RESOURCE_URL_PREFIX = "classpath:";
  private static final String EXPIRY_PROPERTY_PREFIX = "redis.expiryInSeconds.";

  private static Properties cacheProperties = new Properties();

  private RedisCacheUtil() { }

  public static Properties loadCacheProperties(final Properties props) {
    String cachePropsPath = props.getProperty("hibernate.cache.provider_configuration_file_resource_path",
                                              "conf/hibernate-redis.properties");

    InputStream is = null;
    try {
      log.info("Loading cache properties... path={}", cachePropsPath);

      if (cachePropsPath.startsWith(RESOURCE_URL_PREFIX)) {
        // load from resources
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(cachePropsPath);
      } else {
        is = new FileInputStream(new File(new URI(cachePropsPath)));
      }

      cacheProperties.load(is);
    } catch (Exception e) {
      log.error("Fail to load cache properties. path=" + cachePropsPath, e);
    } finally {
      if (is != null) {
        try { is.close(); } catch (Exception ignored) {}
      }
    }

    return cacheProperties;
  }

  public static String getProperty(final String key, String defaultValue) {
    if (cacheProperties == null || cacheProperties.isEmpty()) {
      return defaultValue;
    }
    try {
      String value = cacheProperties.getProperty(key, defaultValue);
      log.trace("get property. key={}, value={}, defaultValue={}", key, value, defaultValue);
      return value;
    } catch (Exception ignored) {
      log.warn("error occurred in reading properties.", ignored);
      return defaultValue;
    }
  }

  public static int getExpireInSeconds(final String region, int defaultExpiryInSeconds) {
    String key = EXPIRY_PROPERTY_PREFIX + region;
    String value = getProperty(key, String.valueOf(defaultExpiryInSeconds));
    return Integer.parseInt(value);
  }
}
