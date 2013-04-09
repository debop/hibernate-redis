package org.hibernate.cache.redis.jedis;

import org.hibernate.cache.redis.IRedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * RedisClient implements using Jedis library
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 9 오후 10:20
 */
public class JedisClient implements IRedisClient {

    private static final Logger log = LoggerFactory.getLogger(JedisClient.class);
    private static final boolean isTranceEnabled = log.isTraceEnabled();
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    private final byte[] rawRegion;
    private final JedisPool jedisPool;

    public JedisClient(String regionName, JedisPool jedisPool) {
        if (isDebugEnabled)
            log.debug("JedisClient created. regionName=[{}]", regionName);

        this.rawRegion = rawKey(regionName);
        this.jedisPool = jedisPool;
    }

    public void put(Object key, Object value) {
        if (isTranceEnabled)
            log.trace("put key=[{}], value=[{}]", key, value);

        Jedis jedis = jedisPool.getResource();

        try {
            byte[] rawKey = rawKey(key);
            byte[] rawValue = rawValue(value);

            jedis.append(rawKey, rawValue);
            jedis.zadd(rawRegion, 0, rawKey);

        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public byte[] rawKey(Object key) {
        return null;
    }

    public byte[] rawValue(Object key) {
        return null;
    }
}
