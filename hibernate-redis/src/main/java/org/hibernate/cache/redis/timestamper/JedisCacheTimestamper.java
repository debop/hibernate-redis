package org.hibernate.cache.redis.timestamper;

import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cfg.Settings;

import java.util.Properties;

public interface JedisCacheTimestamper {

    void setSettings(Settings settings);

    void setProperties(Properties properties);

    void setJedisClient(JedisClient jedisClient);

    /** get next timestamp */
    long next();
}
