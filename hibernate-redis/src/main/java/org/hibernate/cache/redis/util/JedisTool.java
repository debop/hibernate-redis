/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cfg.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import org.hibernate.cache.redis.jedis.JedisCacheKeyCustomizer;

/**
 * Jedis Helper class
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 2. 오전 1:53
 */
@Slf4j
public final class JedisTool {

    public static final String EXPIRY_PROPERTY_PREFIX = "redis.expiryInSeconds.";
    private static final String FILE_URL_PREFIX = "file:";
    private static Properties cacheProperties = null;

    private JedisTool() { }

    /**
     * create {@link org.hibernate.cache.redis.jedis.JedisClient} instance.
     */
    public static JedisClient createJedisClient(Properties props) {
        if (log.isTraceEnabled()){
            log.trace("create JedisClient.");
        }
        Properties cacheProps = loadCacheProperties(props);
        Integer expiryInSeconds = Integer.decode(cacheProps.getProperty("redis.expiryInSeconds", "120"));  // 120 seconds
        cacheProperties = cacheProps;
        
        JedisCacheKeyCustomizer customizer = null;
        String customizerClass = cacheProps.getProperty("redis.customizerClass");
        if (customizerClass != null){
            try {
                customizer = (JedisCacheKeyCustomizer)Class.forName(customizerClass).newInstance();
            } catch (Exception ex) {
                log.error("Problems to instanciate cache key customizer.", ex);
            }
        }

        return new JedisClient(createJedisPool(cacheProps),customizer, expiryInSeconds);
    }

    /**
     * create {@link redis.clients.jedis.JedisPool} instance.
     */
    public static JedisPool createJedisPool(Properties props) {

        String host = props.getProperty("redis.host", "localhost");
        Integer port = Integer.decode(props.getProperty("redis.port", String.valueOf(Protocol.DEFAULT_PORT)));
        Integer timeout = Integer.decode(props.getProperty("redis.timeout", String.valueOf(Protocol.DEFAULT_TIMEOUT))); // msec
        String password = props.getProperty("redis.password", null);
        Integer database = Integer.decode(props.getProperty("redis.database", String.valueOf(Protocol.DEFAULT_DATABASE)));

        if (log.isTraceEnabled()){
            log.trace("create JedisPool. host=[{}], port=[{}], timeout=[{}], password=[{}], database=[{}]",
                 host, port, timeout, password, database);
        }

        return new JedisPool(createJedisPoolConfig(), host, port, timeout, password, database);
    }

    private static JedisPoolConfig createJedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(256);
        poolConfig.setMinIdle(2);
        return poolConfig;
    }

    private static Properties loadCacheProperties(final Properties props) {
        Properties cacheProps = new Properties();
        String cachePath = props.getProperty(Environment.CACHE_PROVIDER_CONFIG,
                                             "hibernate-redis.properties");

        InputStream is = null;
        try {
            if (log.isTraceEnabled()){
                log.trace("Loading cache properties... path=[{}]", cachePath);
            }

            if (cachePath.startsWith(FILE_URL_PREFIX)) {
                // load from file
                is = new FileInputStream(new File(new URI(cachePath)));
            } else {
                // load from resources stream
                is = JedisTool.class.getClassLoader().getResourceAsStream(cachePath);
            }
            cacheProps.load(is);
        } catch (Exception e) {
            log.warn("Fail to load cache properties. cachePath=" + cachePath, e);
        } finally {
            if (is != null) {
                try { is.close(); } catch (Exception ignored) { }
            }
        }
        return cacheProps;
    }

    /**
     * Get expire time out for the specified region
     *
     * @param regionName    region name defined at Entity
     * @param defaultExpiry default expiry in seconds
     * @return expiry in seconds
     */
    public static int getExpireInSeconds(final String regionName, final int defaultExpiry) {
        if (cacheProperties == null)
            return defaultExpiry;
        return Integer.valueOf(getProperty(EXPIRY_PROPERTY_PREFIX + regionName, String.valueOf(defaultExpiry)));
    }

    /**
     * retrieve property value in hibernate-redis.properties
     *
     * @param name         property key
     * @param defaultValue default value
     * @return property value
     */
    public static String getProperty(final String name, final String defaultValue) {
        if (cacheProperties == null)
            return defaultValue;
        try {
            String value = cacheProperties.getProperty(name, defaultValue);
            if (log.isDebugEnabled()){
                log.debug("get property. name=[{}], value=[{}], defaultValue=[{}]", name, value, defaultValue);
            }
            return value;
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

}
