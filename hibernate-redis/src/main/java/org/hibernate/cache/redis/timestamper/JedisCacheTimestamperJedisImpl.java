package org.hibernate.cache.redis.timestamper;

import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Generates increasing identifier for {@link org.hibernate.cache.spi.RegionFactory#nextTimestamp()}.
 * This implementation generates strictly increasing timestamp based on JedisClient nextTimestamp feature.
 */
public class JedisCacheTimestamperJedisImpl implements JedisCacheTimestamper {
    public static final String TIMESTAMP_KEY = "timestamp";

    private Logger log = LoggerFactory.getLogger(JedisCacheTimestamperJedisImpl.class);

    private Settings settings;

    private Properties properties;

    private JedisClient jedisClient;

    private String timestampCacheKey;

    @Override
    public void setSettings(Settings settings) {
        this.settings = settings;
        String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
        timestampCacheKey = cacheRegionPrefix + TIMESTAMP_KEY;
        log.debug("redis cache timestamper redis implementation. timestampCacheKey=[{}]", timestampCacheKey);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void setJedisClient(JedisClient jedisClient) {
        this.jedisClient = jedisClient;
    }

    @Override
    public long next() {
        long next = jedisClient.nextTimestamp(timestampCacheKey);
        log.debug("redis cache timestamper. key=[{}], next=[{}]", timestampCacheKey, next);
        return next;
    }

    public String getTimestampCacheKey() {
        return timestampCacheKey;
    }
}
