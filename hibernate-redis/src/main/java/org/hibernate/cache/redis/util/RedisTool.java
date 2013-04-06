package org.hibernate.cache.redis.util;

import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.serializer.GzipRedisSerializer;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Transaction;

import java.util.Collection;
import java.util.Properties;

/**
 * org.hibernate.cache.redis.util.RedisTool
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오전 10:18
 */
@Slf4j
public class RedisTool {

    private RedisTool() {}

    public static JedisShardInfo createShardInfo(Properties props) {
        String host = props.getProperty("redis.host", "localhost");
        Integer port = Integer.valueOf(props.getProperty("redis.port", "6379"));
        Integer timeout = Integer.valueOf(props.getProperty("redis.timeout", "2000"));

        return new JedisShardInfo(host, port, timeout);
    }

    public static JedisConnectionFactory createConnectionFactory(Properties props) {
        JedisConnectionFactory factory = new JedisConnectionFactory(createShardInfo(props));

        Integer database = Integer.valueOf(props.getProperty("redis.database", "0"));
        Boolean usePool = Boolean.valueOf(props.getProperty("redis.usePool", "true"));
        factory.setDatabase(database);
        factory.setUsePool(usePool);

        return factory;
    }

    public static RedisClient createRedisClient(Properties props) {
        if (log.isInfoEnabled())
            log.info("RedisClient 를 생성합니다...");
        RedisClient redis = new RedisClient();
        redis.setConnectionFactory(createConnectionFactory(props));

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        redis.setKeySerializer(keySerializer);
        redis.setHashKeySerializer(keySerializer);

        RedisSerializer valueSerializer = new GzipRedisSerializer(new JdkSerializationRedisSerializer());
        redis.setValueSerializer(valueSerializer);
        redis.setHashValueSerializer(valueSerializer);

        redis.afterPropertiesSet();
        return redis;
    }

    /**
     * RedisTemplate 는 Connection Pool 을 사용하므로, Transaction을 사용하기 위해서는 한 Session에서 작업하도록 해야 합니다.
     */
    @SuppressWarnings("unchecked")
    public static <T> T withinTx(RedisTemplate redis,
                                 final SessionCallback<T> callback) throws CacheException {
        if (log.isTraceEnabled())
            log.debug("RedisClient 작업을 Transaction 하에서 수행합니다.");

        return (T) redis.execute(new SessionCallback<T>() {
            @Override
            public T execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                T result = (T) callback.execute(operations);
                operations.exec();
                return result;
            }
        });
    }

    /**
     * Redis Java Client 인 Jedis 를 생성합니다.
     *
     * @param props
     * @return
     */
    public static Jedis createJedis(Properties props) {
        if (log.isDebugEnabled())
            log.debug("Create Jedis instance...");

        String host = props.getProperty("redis.host", "localhost");
        Integer port = Integer.valueOf(props.getProperty("redis.port", "6379"));
        Integer timeout = Integer.valueOf(props.getProperty("redis.timeout", "2000"));
        Integer database = Integer.valueOf(props.getProperty("redis.database", "0"));
        Boolean usePool = Boolean.valueOf(props.getProperty("redis.usePool", "true"));

        if (log.isInfoEnabled())
            log.info("Jedis instance. host=[{}], port=[{}], timeout=[{}], database=[{}], usePool=[{}]",
                     host, port, timeout, database, usePool);

        JedisShardInfo shard = new JedisShardInfo(host, port, timeout);

        return new Jedis(shard);
    }

    /**
     * Transaction 하에서 작업을 수행합니다.
     */
    public static <T> T withinTx(Jedis jedis, Function<Jedis, T> function) throws Exception {
        T result = null;
        Transaction tx = jedis.multi();
        try {
            result = function.apply(jedis);
            tx.exec();
        } catch (Exception e) {
            tx.discard();
            if (log.isWarnEnabled())
                log.warn("Transaction 하에서 작업 중에 실패했습니다.", e);
            throw e;
        }
        return result;
    }

    public static String[] toArray(Collection<String> strs) {
        final String[] arr = new String[strs.size()];

        int i = 0;
        for (final String str : strs) {
            arr[i++] = str;
        }
        return arr;
    }
}
