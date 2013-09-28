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
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.serializer.BinaryRedisSerializer;
import org.hibernate.cache.redis.serializer.RedisSerializer;
import org.hibernate.cache.redis.serializer.SerializationTool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RedisClient implements using Jedis library
 * <p/>
 * 참고 : https://github.com/xetorthio/jedis/wiki/AdvancedUsage
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 13. 4. 9 오후 10:20
 */
@Slf4j
public class JedisClient {
    public static final int DEFAULT_EXPIRY_IN_SECONDS = 120;
    public static final String DEFAULT_REGION_NAME = "hibernate";

    @Getter
    private final JedisPool jedisPool;

    @Getter
    @Setter
    private int database;

    @Getter
    @Setter
    private int expiryInSeconds;

    @Getter
    @Setter
    private RedisSerializer<Object> keySerializer = new BinaryRedisSerializer<Object>();

    @Getter
    @Setter
    private RedisSerializer<Object> valueSerializer = new BinaryRedisSerializer<Object>();

    public JedisClient() {
        this(new JedisPool("localhost"));
    }

    public JedisClient(JedisPool jedisPool) {
        this(jedisPool, DEFAULT_EXPIRY_IN_SECONDS);
    }

    public JedisClient(JedisPool jedisPool, int expiryInSeconds) {
        log.debug("JedisClient created. jedisPool=[{}], expiryInSeconds=[{}]",
                  jedisPool, expiryInSeconds);

        this.jedisPool = jedisPool;
        this.expiryInSeconds = expiryInSeconds;
    }

    /**
     * e     * 서버와의 통신 테스트, "PONG" 을 반환한다
     */
    public String ping() {
        return run(new JedisCallback<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.ping();
            }
        });
    }

    /**
     * db size를 구합니다.
     */
    public Long dbSize() {
        return run(new JedisCallback<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.dbSize();
            }
        });
    }

    /**
     * 키에 해당하는 캐시 값이 존재하는지 확인합니다.
     *
     * @param region 영역 명
     * @param key    캐시 키
     */
    public Boolean exists(String region, Object key) {
        log.trace("캐시값 존재여부를 확인합니다. region=[{}], key=[{}]", region, key);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawKey = rawKey(key);

        return run(new JedisCallback<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.hexists(rawRegion, rawKey);
            }
        });
    }

    /**
     * 키에 해당하는 캐시 값을 구합니다.
     *
     * @param region 영역 명
     * @param key    캐시 키
     * @return 저장된 캐시 값, 없으면 null을 반환한다.
     */
    public Object get(String region, Object key) {
        log.trace("캐시값을 조회합니다... region=[{}], key=[{}]", region, key);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawKey = rawKey(key);

        byte[] rawValue = run(new JedisCallback<byte[]>() {
            @Override
            public byte[] execute(Jedis jedis) {
                return jedis.hget(rawRegion, rawKey);
            }
        });

        Object value = deserializeValue(rawValue);
        log.trace("캐시값을 조회했습니다. region=[{}], key=[{}], value=[{}]", region, key, value);
        return value;
    }

    /**
     * 영역에 존재하는 모든 캐시 키를 반환합니다.
     *
     * @param region 영역
     * @return 저장된 캐시 키의 컬렉션
     */
    public Set<Object> keysInRegion(String region) {
        log.trace("영역에 해당하는 모든 키를 가져옵니다. region=[{}]", region);

        final byte[] rawRegion = rawRegion(region);
        Set<byte[]> rawKeys = run(new JedisCallback<Set<byte[]>>() {
            @Override
            public Set<byte[]> execute(Jedis jedis) {
                return jedis.hkeys(rawRegion);
            }
        });
        return deserializeKeys(rawKeys);
    }

    /**
     * 지정된 영역의 모든 캐시 항목을 가져옵니다.
     *
     * @param region 영역
     * @return 모든 캐시 항목 정보
     */
    public Map<Object, Object> hgetAll(String region) {
        log.trace("영역에 해당하는 key-value 를 조회합니다. region=[{}]", region);

        final byte[] rawRegion = rawRegion(region);
        Map<byte[], byte[]> rawMap = run(new JedisCallback<Map<byte[], byte[]>>() {
            @Override
            public Map<byte[], byte[]> execute(Jedis jedis) {
                return jedis.hgetAll(rawRegion);
            }
        });

        Map<Object, Object> map = new HashMap<Object, Object>();
        for (Map.Entry<byte[], byte[]> entry : rawMap.entrySet()) {
            Object key = deserializeKey(entry.getKey());
            Object value = deserializeValue(entry.getValue());
            map.put(key, value);
        }
        return map;
    }

    /**
     * 지정한 키들의 값들을 한꺼번에 가져옵니다.
     *
     * @param region 영역 명
     * @param keys   캐시 키 컬렉션
     * @return 캐시 값의 컬렉션
     */
    public List<Object> mget(String region, Collection<?> keys) {
        log.trace("영역에서 해당 키에 해당하는 값 들을 조회합니다. region=[{}], keys=[{}]", region, keys);

        final byte[] rawRegion = rawRegion(region);
        final byte[][] rawKeys = rawKeys(keys);

        List<byte[]> rawValues = run(new JedisCallback<List<byte[]>>() {
            @Override
            public List<byte[]> execute(Jedis jedis) {
                return jedis.hmget(rawRegion, rawKeys);
            }
        });
        return deserializeValues(rawValues);
    }

    /**
     * 캐시를 저장합니다.
     *
     * @param region 영역 명
     * @param key    캐시 키
     * @param value  캐시 값
     */
    public void set(String region, Object key, Object value) {
        set(region, key, value, expiryInSeconds, TimeUnit.SECONDS);
    }

    /**
     * 캐시를 저장합니다.
     *
     * @param region           영역 명
     * @param key              캐시 키
     * @param value            캐시 값
     * @param timeoutInSeconds 유효 기간
     */
    public void set(String region, Object key, Object value, long timeoutInSeconds) {
        set(region, key, value, timeoutInSeconds, TimeUnit.SECONDS);
    }

    /**
     * 캐시를 저장합니다.
     *
     * @param region  영역 명
     * @param key     캐시 키
     * @param value   캐시 값
     * @param timeout 유효기간
     * @param unit    유효기간 단위
     */
    public void set(final String region, final Object key, final Object value, long timeout, TimeUnit unit) {
        log.trace("항목을 캐싱합니다. region=[{}], key=[{}], value=[{}], timeout=[{}], unit=[{}]",
                  region, key, value, timeout, unit);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawKey = rawKey(key);
        final byte[] rawValue = rawValue(value);
        final int seconds = (int) unit.toSeconds(timeout);

        runWithTx(new JedisTransactionalCallback() {
            @Override
            public void execute(Transaction tx) {
                tx.hset(rawRegion, rawKey, rawValue);
                // Expire 될 정보를 Scored Set 에 저장한다.
                if (seconds > 0) {
                    final byte[] rawZkey = rawZkey(region);
                    final long score = new Date().getTime() + seconds * 1000L;
                    tx.zadd(rawZkey, score, rawKey);
                }
            }
        });
    }

    /**
     * 현 영역에 있는 캐시 항목 중 Expire 된 항목을 삭제합니다.
     *
     * @param region 영역 명
     */
    public void expire(final String region) {
        // score 가 현재 시각보다 작은 값을 가진 member 를 추려내, 삭제한다.

        final byte[] rawZkey = rawZkey(region);
        final long score = new Date().getTime();
        final byte[] rawRegion = rawRegion(region);

        log.debug("region[{}]의 정보 중 expires 될 정보가 있다면 삭제합니다... expire time=[{}]", region, score);

        run(new JedisCallback<Object>() {
            @Override
            public Object execute(Jedis jedis) {
                // expire되어야 할 key를 조회합니다.
                Set<byte[]> rawKeys = jedis.zrangeByScore(rawZkey, 0, score);

                if (rawKeys != null) {
                    // 해당 캐시를 실제로 삭제합니다.
                    for (byte[] rawKey : rawKeys) {
                        jedis.hdel(rawRegion, rawKey);
                    }
                    // Expire 된 key의 expiration 정보를 삭제합니다.
                    jedis.zremrangeByScore(rawZkey, 0, score);
                }
                return null;
            }
        });
    }

    /**
     * 지정된 키의 항목으로 삭제합니다.
     *
     * @param region 영역 명
     * @param key    삭제할 캐시 키
     */
    public Long del(String region, Object key) {
        log.trace("캐시를 삭제합니다. region=[{}], key=[{}]", region, key);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawKey = rawKey(key);

        final byte[] rawZkey = rawZkey(region);

        runWithTx(new JedisTransactionalCallback() {
            @Override
            public void execute(Transaction tx) {
                tx.hdel(rawRegion, rawKey);
                tx.zrem(rawZkey, rawKey);
            }
        });

        return 1L;
    }

    /**
     * 지정된 키의 항목으로 삭제합니다.
     *
     * @param keys 삭제할 키의 컬렉션
     */
    public void mdel(String region, Collection<?> keys) {
        log.trace("복수의 캐시를 삭제합니다. region=[{}], keys=[{}]", region, keys);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawZkey = rawZkey(region);
        final byte[][] rawKeys = rawKeys(keys);

        runWithTx(new JedisTransactionalCallback() {
            @Override
            public void execute(Transaction tx) {
                for (byte[] rawKey : rawKeys) {
                    tx.hdel(rawRegion, rawKey);
                    tx.zrem(rawZkey, rawKey);
                }
            }
        });
    }

    /**
     * 해당 영역의 모든 캐시 항목을 삭제합니다.
     */
    public void deleteRegion(final String region) throws CacheException {
        log.debug("Region 전체를 삭제합니다... region=[{}]", region);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawZkey = rawZkey(region);

        runWithTx(new JedisTransactionalCallback() {
            @Override
            public void execute(Transaction tx) {
                tx.del(rawRegion);
                tx.del(rawZkey);
            }
        });
    }

    /**
     * DB의 모든 내용을 삭제합니다.
     * NOTE: 다른 데이터와 같이 사용하는 DB인 경우 삭제될 위험이 있습니다.
     */
    public String flushDb() {
        log.info("Redis DB 전체를 flush 합니다...");

        return run(new JedisCallback<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.flushDB();
            }
        });
    }

    /**
     * 키를 byte[] 로 직렬화합니다 *
     */
    private byte[] rawKey(Object key) {
        return getKeySerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    private byte[][] rawKeys(Collection<?> keys) {
        byte[][] rawKeys = new byte[keys.size()][];
        int i = 0;
        for (Object key : keys) {
            rawKeys[i++] = getKeySerializer().serialize(key);
        }
        return rawKeys;
    }

    private byte[] rawZkey(String region) {
        return rawRegion("z:" + region);
    }

    /**
     * 키를 이용해 region 값을 직렬화합니다.
     */
    private byte[] rawRegion(String region) {
        return region.getBytes();
    }

    /**
     * byte[] 를 key 값으로 역직렬화 합니다
     */
    private Object deserializeKey(byte[] rawKey) {
        return getKeySerializer().deserialize(rawKey);
    }

    /**
     * 캐시 값을 byte[]로 직렬화를 수행합니다.
     */
    private byte[] rawValue(Object value) {
        return getValueSerializer().serialize(value);
    }

    /**
     * byte[] 를 역직렬화하여 원 객체로 변환합니다.
     */
    private Object deserializeValue(byte[] rawValue) {
        return getValueSerializer().deserialize(rawValue);
    }

    /**
     * Redis 작업을 수행합니다.<br/>
     * {@link redis.clients.jedis.JedisPool} 을 이용하여, {@link redis.clients.jedis.Jedis}를 풀링하여 사용하도록 합니다.
     */
    private <T> T run(final JedisCallback<T> callback) {

        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(database);
            return callback.execute(jedis);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 복수의 작업을 하나의 Transaction 하에서 수행하도록 합니다.<br />
     * {@link redis.clients.jedis.JedisPool} 을 이용하여, {@link redis.clients.jedis.Jedis}를 풀링하여 사용하도록 합니다.
     */
    private List<Object> runWithTx(final JedisTransactionalCallback callback) {

        Jedis jedis = jedisPool.getResource();
        try {
            Transaction tx = jedis.multi();
            tx.select(database);
            callback.execute(tx);
            return tx.exec();
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * Raw Key 값들을 역직렬화하여 Key Set을 반환합니다.
     */
    private Set<Object> deserializeKeys(Set<byte[]> rawKeys) {
        return SerializationTool.deserialize(rawKeys, getKeySerializer());
    }

    /**
     * Raw Value 값들을 역직렬화하여 Value List를 반환합니다.
     */
    private List<Object> deserializeValues(List<byte[]> rawValues) {
        return SerializationTool.deserialize(rawValues, getValueSerializer());
    }
}
