package org.hibernate.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * Redis Client 입니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 1:30
 */
@Slf4j
public class RedisClient extends RedisTemplate<String, Object> {

    public long dbSize() {
        return execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.dbSize();
            }
        });
    }

    public boolean exists(final String key) {
        return execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] rawKey = rawKey(key);
                return connection.exists(rawKey);
            }
        });
    }

    public Object get(String key) {
        return opsForValue().get(key);
    }

    /**
     * Multi Get
     *
     * @param keys
     * @return
     */
    public List<Object> mget(final Collection<String> keys) {
        return opsForValue().multiGet(keys);
    }


    @SuppressWarnings("unchecked")
    private byte[] rawKey(String key) {
        Assert.notNull(key, "non null key required");
        return ((RedisSerializer) getKeySerializer()).serialize(key);
    }

    private byte[] rawString(String key) {
        return getStringSerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    private byte[] rawValue(Object value) {
        return ((RedisSerializer) getValueSerializer()).serialize(value);
    }

    private byte[][] rawKeys(Collection<String> keys) {
        final byte[][] rawKeys = new byte[keys.size()][];

        int i = 0;
        for (String key : keys) {
            rawKeys[i++] = rawKey(key);
        }
        return rawKeys;
    }

    @SuppressWarnings("unchecked")
    private String deserializeKey(byte[] value) {
        return (String) getKeySerializer().deserialize(value);
    }
}
