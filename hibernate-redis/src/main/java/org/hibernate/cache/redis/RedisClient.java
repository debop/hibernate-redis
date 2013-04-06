package org.hibernate.cache.redis;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.util.RedisTool;
import org.hibernate.cache.spi.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis Client 입니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 1:30
 */
public class RedisClient extends RedisTemplate<String, Object> {

    private static final Logger log = LoggerFactory.getLogger(RedisClient.class);
    private static final boolean isTranceEnabled = log.isTraceEnabled();
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    public long dbSize() {
        return execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.dbSize();
            }
        });
    }

    public boolean exists(final String key) {
        boolean result = hasKey(key);
        if (isTranceEnabled)
            log.trace("exists key=[{}], result=[{}]", key, result);
        return result;
    }

    /**
     * Get value
     */
    public Object get(final String key) {
        Object value = opsForValue().get(key);
        if (isTranceEnabled)
            log.trace("get key=[{}] ... return value=[{}]", key, value);
        return value;
    }

    /**
     * Multi Get
     */
    public List<Object> mget(final Collection<String> keys) {
        if (isTranceEnabled)
            log.trace("multi get... keys=[{}]", keys);
        return opsForValue().multiGet(keys);
    }

    public void set(String key, Object value) {
        if (isTranceEnabled)
            log.trace("set key=[{}], value=[{}]", key, value);

        opsForValue().set(key, value);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        if (isTranceEnabled)
            log.trace("set key=[{}], value=[{}], timeout=[{}], unit=[{}]", key, value, timeout, unit);
        opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public void delete(final String key) {
        if (isTranceEnabled)
            log.trace("delete key=[{}]", key);

        super.delete(key);
    }

    /**
     * Region 에 해당하는 모든 항목을 삭제합니다.
     */
    public void deleteRegion(final String regionPrefix) throws CacheException {
        if (isDebugEnabled)
            log.debug("Region을 Clear 합니다. Region=[{}]", regionPrefix);

        try {
            RedisTool.withinTx(this, new SessionCallback<Void>() {
                @Override
                public Void execute(RedisOperations operations) throws DataAccessException {
                    Set<String> keys = operations.keys(regionPrefix + "*");
                    operations.delete(keys);
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("Region을 삭제하는데 실패했습니다.", e);
            throw new CacheException(e);
        }
    }

    public void deleteRegion(Region region) throws CacheException {
        deleteRegion(region.getName());
    }

    public void flushDb() {
        if (isDebugEnabled)
            log.debug("DB를 Flush 합니다...");

        execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return null;
            }
        });
    }


    @SuppressWarnings("unchecked")
    private byte[] rawKey(String key) {
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
