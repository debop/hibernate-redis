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

package org.hibernate.cache.redis.jedis;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.cache.redis.IRedisClient;
import org.hibernate.cache.redis.serializer.BinaryRedisSerializer;
import org.hibernate.cache.redis.serializer.RedisSerializer;
import org.hibernate.cache.redis.serializer.SerializationTool;
import org.hibernate.cache.spi.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * RedisClient implements using Jedis library
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 9 오후 10:20
 */
public class JedisClient implements IRedisClient {

    private static final Logger log = LoggerFactory.getLogger(JedisClient.class);
    private static final boolean isTraceEnabled = log.isTraceEnabled();
    private static final boolean isDebugEnabled = log.isDebugEnabled();

    private static final int DEFAULT_EXPIRY_IN_SECONDS = 120;

    public static String getEntityName(Object key) {
        return ((CacheKey) key).getEntityOrRoleName();
    }

    @Getter
    private final byte[] rawRegion;

    @Getter
    private final JedisPool jedisPool;

    @Getter
    @Setter
    private int expiryInSeconds;

    @Getter
    @Setter
    private RedisSerializer keySerializer = new BinaryRedisSerializer<Object>();

    @Getter
    @Setter
    private RedisSerializer valueSerializer = new BinaryRedisSerializer<Object>();

    public JedisClient(JedisPool jedisPool) {
        this("hibernate", jedisPool);
    }

    public JedisClient(String regionName, JedisPool jedisPool) {
        this(regionName, jedisPool, DEFAULT_EXPIRY_IN_SECONDS);
    }

    public JedisClient(String regionName, JedisPool jedisPool, int expiryInSeconds) {
        log.debug("JedisClient created. regionName=[{}]", regionName);

        this.rawRegion = rawKey(regionName);
        this.jedisPool = jedisPool;
    }

    /** db size를 구합니다. */
    public Long dbSize() {
        return run(new JedisCallback<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.dbSize();
            }
        });
    }

    /** 키에 해당하는 캐시 값이 존재하는지 확인합니다. */
    public boolean exists(Object key) {
        final byte[] rawKey = rawKey(getEntityName(key));
        final byte[] rawMember = rawValue(key);
        Long rank = run(new JedisCallback<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.zrank(rawKey, rawMember);
            }
        });
        if (isTraceEnabled) log.trace("캐시 값이 존재하는지 확인합니다. key=[{}], exists=[{}]", key, (rank != null));
        return rank != null;
    }

    /**
     * 키에 해당하는 캐시 값을 구합니다.
     *
     * @param key 캐시 키
     * @return 저장된 캐시 값, 없으면 null을 반환한다.
     */
    public Object get(Object key) {
        if (isTraceEnabled) log.trace("get value... key=[{}]", key);

        final byte[] rawKey = rawKey(key);
        byte[] rawValue = run(new JedisCallback<byte[]>() {
            @Override
            public byte[] execute(Jedis jedis) {
                return jedis.get(rawKey);
            }
        });
        return deserializeValue(rawValue);
    }

    /**
     * 지정한 캐시 영역에 저장된 캐시의 키 집합을 반환합니다.
     *
     * @param regionName 캐시 영역명
     * @return 캐시 영역에 저장된 모든 키 정보
     */
    public Set<Object> keysInRegion(String regionName) {
        if (isTraceEnabled) log.trace("영역에 해당하는 모든 키 값을 가져옵니다. regionName=[{}]", regionName);

        final byte[] rawRegion = rawKey(regionName);
        Set<byte[]> rawKeys = run(new JedisCallback<Set<byte[]>>() {
            @Override
            public Set<byte[]> execute(Jedis jedis) {
                return jedis.zrange(rawRegion, 0, -1);

            }
        });
        return deserializeKeys(rawKeys);
    }

    /**
     * 지정한 키들의 값들을 한꺼번에 가져옵니다.
     *
     * @param keys 캐시 키 컬렉션
     * @return 캐시 값의 컬렉션
     */
    public List<Object> mget(Collection<Object> keys) {
        if (isTraceEnabled) log.trace("multi get... keys=[{}]", Arrays.toString(keys.toArray()));

        final byte[][] rawKeys = new byte[keys.size()][];
        int i = 0;
        for (Object key : keys) {
            rawKeys[i] = rawKey(key);
        }

        List<byte[]> rawValues = run(new JedisCallback<List<byte[]>>() {
            @Override
            public List<byte[]> execute(Jedis jedis) {
                return jedis.mget(rawKeys);
            }
        });

        return deserializeValues(rawValues);
    }

    /**
     * 캐시를 저장합니다.
     *
     * @param key   캐시 키
     * @param value 캐시 값
     */
    public void set(Object key, Object value) {
        set(key, value, expiryInSeconds, TimeUnit.SECONDS);
    }

    /**
     * 캐시를 저장합니다.
     *
     * @param key     캐시 키
     * @param value   캐시 값
     * @param timeout 캐시 유효 시간
     * @param unit    시간 단위 (기본은 seconds)
     */
    public void set(Object key, Object value, long timeout, TimeUnit unit) {
        if (isTraceEnabled)
            log.trace("캐시를 저장합니다... key=[{}], value=[{}]", key, value);

        final byte[] rawKey = rawKey(key);
        final byte[] rawValue = rawValue(value);
        final int seconds = (int) unit.toSeconds(timeout);

        runWithTx(new JedisCallback<Void>() {
            @Override
            public Void execute(Jedis jedis) {
                jedis.set(rawKey, rawValue);
                jedis.expire(rawKey, seconds);
                jedis.zadd(rawRegion, 0, rawKey);
                jedis.expire(rawRegion, seconds);
                return null;
            }
        });
    }

    /** 키를 byte[] 로 직렬화합니다 * */
    @SuppressWarnings( "unchecked" )
    private byte[] rawKey(Object key) {
        return getKeySerializer().serialize(key);
    }

    /** byte[] 를 key 값으로 역직렬화 합니다 */
    private Object deserializeKey(byte[] rawKey) {
        return getKeySerializer().deserialize(rawKey);
    }

    /** 캐시 값을 byte[]로 직렬화를 수행합니다. */
    @SuppressWarnings( "unchecked" )
    private byte[] rawValue(Object value) {
        return getValueSerializer().serialize(value);
    }

    /** byte[] 를 역직렬화하여 원 객체로 변환합니다. */
    private Object deserializeValue(byte[] rawValue) {
        return getValueSerializer().deserialize(rawValue);
    }

    /**
     * Redis 작업을 수행합니다.<br/>
     * {@link JedisPool} 을 이용하여, {@link Jedis}를 풀링하여 사용하도록 합니다.
     */
    private <T> T run(final JedisCallback<T> callback) {
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
     * 복수의 작업을 하나의 Transaction 하에서 수행하도록 합니다.<br />
     * {@link JedisPool} 을 이용하여, {@link Jedis}를 풀링하여 사용하도록 합니다.
     */
    private <T> T runWithTx(final JedisCallback<T> callback) {
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

    /** Raw Key 값들을 역직렬화하여 Key Set을 반환합니다. */
    @SuppressWarnings( "unchecked" )
    private Set<Object> deserializeKeys(Set<byte[]> rawKeys) {
        return SerializationTool.deserialize(rawKeys, getKeySerializer());
    }

    /** Raw Value 값들을 역직렬화하여 Value List를 반환합니다. */
    @SuppressWarnings( "unchecked" )
    private List<Object> deserializeValues(List<byte[]> rawValues) {
        return SerializationTool.deserialize(rawValues, getValueSerializer());
    }
}
