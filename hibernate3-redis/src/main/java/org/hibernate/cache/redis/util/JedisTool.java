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
import org.apache.commons.pool.impl.GenericObjectPool;
import org.hibernate.cache.redis.jedis.JedisClient;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * org.hibernate.cache.redis.util.JedisTool
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 5. 2. 오전 1:53
 */
@Slf4j
public final class JedisTool {

    private JedisTool() {
    }

    /**
     * The Hibernate system property specifying the location of the redis configuration file name.
     * <p/>
     * If not set, redis.xml will be looked for in the root of the classpath.
     * <p/>
     * If set to say redis-1.xml, redis-1.xml will be looked for in the root of the classpath.
     */
    public static final String IO_REDIS_CACHE_CONFIGURATION_RESOURCE_NAME = "io.redis.cache.configurationResourceName";

    public static final String DEFAULT_CLASSPATH_CONFIGURATION_FILE = "redis.xml";

    /**
     * {@link redis.clients.jedis.JedisPool} 을 생성합니다.
     */
    public static JedisPool createJedisPool(Properties props) {
        String configurationResourceName = null;
        if (props != null) {
            configurationResourceName = (String) props.get(IO_REDIS_CACHE_CONFIGURATION_RESOURCE_NAME);
        }
        URL url = null;
        if (configurationResourceName == null || configurationResourceName.length() == 0) {
            ClassLoader standardClassloader = getStandardClassLoader();
            if (standardClassloader != null) {
                url = standardClassloader.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
            }
        } else {
            try {
                url = new URL(configurationResourceName);
            } catch (MalformedURLException e) {
                url = loadResource(configurationResourceName);
            }
        }

        Properties property = new Properties();
        try {
            if(url.getFile().indexOf(".xml") >= 0) {
                property.loadFromXML(new FileInputStream(new File(url.getFile())));
            } else {
                property.load(new FileInputStream(new File(url.getFile())));
            }
        } catch (IOException e) {
            log.warn("not found configuration of redis. localhost setting");
        }

        String host = property.getProperty("redis.host", "localhost");
        Integer port = Integer.decode(property.getProperty("redis.port", "6379"));
        Integer timeout = Integer.decode(property.getProperty("redis.timeout", "2000")); // msec
        String password = property.getProperty("redis.password", null);
        Integer database = Integer.decode(property.getProperty("redis.database", "0"));

        log.info("JedisPool을 생성합니다... host=[{}], port=[{}], timeout=[{}], password=[{}], database=[{}]",
                host, port, timeout, password, database);

        return new JedisPool(new GenericObjectPool.Config(), host, port, timeout, password, database);
    }

    protected static URL loadResource(String configurationResourceName) {
        ClassLoader standardClassloader = getStandardClassLoader();
        URL url = null;
        if (standardClassloader != null) {
            url = standardClassloader.getResource(configurationResourceName);
        }
        return url;
    }

    private static ClassLoader getStandardClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * {@link JedisClient} 를 생성합니다.
     */
    public static JedisClient createJedisClient(Properties props) {
        return createJedisClient(JedisClient.DEFAULT_REGION_NAME, props);
    }

    /**
     * {@link JedisClient} 를 생성합니다.
     */
    public static JedisClient createJedisClient(String regionName, Properties props) {
        Integer expiryInSeconds = Integer.decode(props.getProperty("redis.expiryInSeconds", "120"));  // 120 seconds
        return new JedisClient(regionName, createJedisPool(props), expiryInSeconds);
    }
}
