package org.hibernate.cache.redis.util;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Transaction;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * org.hibernate.cache.redis.util.RedisTool
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오전 10:18
 */
@Slf4j
public class RedisTool {

    private RedisTool() {}

    private static final StringSerializer keySerializer = new StringSerializer();
    private static final JsonSerializer valueSerializer = new JsonSerializer();

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

    public static <T> T withinTx(Jedis jedis, Callable<T> callable) throws Exception {
        T result = null;
        Transaction tx = jedis.multi();
        try {
            result = callable.call();
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

    public static byte[] serializeString(String str) {
        return keySerializer.serialize(str);
    }

    public static String deserializeToString(byte[] bytes) {
        return keySerializer.deserialize(bytes);
    }

    public static byte[][] serializeStringList(Collection<String> strs) {
        final byte[][] raws = new byte[strs.size()][];

        int i = 0;
        for (String str : strs) {
            raws[i++] = serializeString(str);
        }
        return raws;
    }

    public static String serializeValue(Object value) {
        return valueSerializer.serializeToText(value);
    }

    public static <T> T deserializeValue(Class<T> clazz, String text) {
        return valueSerializer.deserializeFromText(text, clazz);
    }
}
