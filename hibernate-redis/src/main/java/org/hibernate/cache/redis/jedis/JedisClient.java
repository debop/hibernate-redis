package org.hibernate.cache.redis.jedis;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.cache.redis.IRedisClient;
import org.hibernate.cache.redis.serializer.BinaryRedisSerializer;
import org.hibernate.cache.redis.serializer.IRedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

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

    @Getter
    private final byte[] rawRegion;
    @Getter
    private final JedisPool jedisPool;

    @Getter
    @Setter
    IRedisSerializer valueSerializer = new BinaryRedisSerializer();

    public JedisClient(String regionName, JedisPool jedisPool) {
        if (isDebugEnabled)
            log.debug("JedisClient created. regionName=[{}]", regionName);

        this.rawRegion = rawKey(regionName);
        this.jedisPool = jedisPool;
    }

    public Object get(Object key) {
        if (isTranceEnabled)
            log.trace("get value... key=[{}]", key);

        final byte[] rawKey = rawKey(key);
        final byte[] rawValue = execute(new IJedisCallback<byte[]>() {
            @Override
            public byte[] execute(Jedis jedis) {
                return jedis.get(rawKey);
            }
        });

        return getValueSerializer().deserialize(rawValue);
    }

    public void put(Object key, Object value) {
        if (isTranceEnabled)
            log.trace("put key=[{}], value=[{}]", key, value);

        final byte[] rawKey = rawKey(key);
        final byte[] rawValue = rawValue(value);

        withTx(new IJedisCallback<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                jedis.append(rawKey, rawValue);
                jedis.zadd(rawRegion, 0, rawKey);
                return null;
            }
        });
    }

    private byte[] rawKey(Object key) {
        // TODO: Implementation
        return null;
    }

    private byte[] rawValue(Object key) {
        // TODO: Implementation
        return null;
    }

    /**
     * Redis 작업을 수행합니다. {@link JedisPool} 을 이용하여, {@link Jedis}를 풀링하여 사용하도록 합니다.
     */
    private <T> T execute(final IJedisCallback<T> callback) {
        Jedis jedis = jedisPool.getResource();

        try {
            return callback.execute(jedis);
        } catch (Throwable t) {
            log.error("Redis 작업 중 예외가 발생했습니다.", t);
            throw new RuntimeException(t);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 복수의 작업을 하나의 Transaction 하에서 수행하도록 합니다.
     */
    private <T> T withTx(final IJedisCallback<T> callback) {
        Jedis jedis = jedisPool.getResource();
        try {
            Transaction tx = jedis.multi();
            T result = callback.execute(jedis);
            tx.exec();
            return result;
        } catch (Throwable t) {
            log.error("Redis 작업 중 예외가 발생했습니다.", t);
            throw new RuntimeException(t);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }
}
