package org.hibernate.cache.redis.timestamper;

import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * next timestamp based System.currentTimeMillis
 * <p/>
 * If strictly increasing timestamp required, use {@link JedisCacheTimestamperJedisImpl}.
 *
 */
public class JedisCacheTimestamperJvmImpl implements JedisCacheTimestamper {
    private Logger log = LoggerFactory.getLogger(JedisCacheTimestamperJvmImpl.class);

    @Override
    public void setSettings(Settings settings) {
        // no op
    }

    @Override
    public void setProperties(Properties properties) {
        // no op
    }

    @Override
    public void setJedisClient(JedisClient jedisClient) {
        // no op
    }

    @Override
    public long next() {
        long next = System.currentTimeMillis();
        log.trace("redis cache timestamper next : {}", next);
        return next;
    }
}
