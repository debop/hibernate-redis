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
import org.hibernate.cache.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
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
    public static final String EXPIRY_POSTFIX = ":expire";

    public static String getExpireRegion(final String region) {
        return region + EXPIRY_POSTFIX;
    }

    public static String getExpireKey(Object key) {
        return (key != null) ? key.toString() : "";
    }

    @Getter
    private final JedisPool jedisPool;

    @Getter
    @Setter
    private int expiryInSeconds;

    @Getter
    private StringRedisSerializer regionSerializer = new StringRedisSerializer();

    @Getter
    private RedisSerializer<Object> keySerializer = new BinaryRedisSerializer<Object>();

    @Getter
    private RedisSerializer<Object> valueSerializer = new BinaryRedisSerializer<Object>();

    public JedisClient() {
        this(new JedisPool("localhost"), DEFAULT_EXPIRY_IN_SECONDS);
    }

    public JedisClient(JedisPool jedisPool) {
        this(jedisPool, DEFAULT_EXPIRY_IN_SECONDS);
    }

    /**
     * initialize JedisClient instance
     *
     * @param jedisPool       JedisPool instance
     * @param expiryInSeconds expiration in seconds
     */
    public JedisClient(JedisPool jedisPool, int expiryInSeconds) {
        log.debug("JedisClient created. jedisPool=[{}], expiryInSeconds=[{}]", jedisPool, expiryInSeconds);

        this.jedisPool = jedisPool;
        this.expiryInSeconds = expiryInSeconds;
    }

    /**
     * ping test for server alive
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
     * get Redis db size
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
     * confirm the specified cache item in specfied region
     *
     * @param region region name
     * @param key    cache key
     */
    public boolean exists(final String region, final Object key) {
        log.trace("confirm cache item. region=[{}], key=[{}]", region, key);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawKey = rawKey(key);

        boolean valueExists = run(new JedisCallback<Boolean>() {
            @Override
            public Boolean execute(Jedis jedis) {
                return jedis.hexists(rawRegion, rawKey);
            }
        });
        return valueExists && !isExpired(region, key);
    }

    /**
     * decide the specified cache key is expired.
     *
     * @param region region name
     * @param key    cache key
     * @return is expired
     */
    public boolean isExpired(final String region, final Object key) {
        if (key == null)
            return true;
        final String timestampStr = run(new JedisCallback<String>() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.hget(getExpireRegion(region), getExpireKey(key));
            }
        });
        try {
            if (timestampStr == null || timestampStr.isEmpty())
                return false;
            Long timestamp = Long.parseLong(timestampStr);
            if (timestamp != 0 && timestamp <= System.currentTimeMillis()) {
                // if cache is expired, delete cache
                del(region, key);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Get cache
     *
     * @param region region name
     * @param key    cache key
     * @return return cached entity, if not exists return null.
     */
    public Object get(final String region, final Object key) {
        log.trace("retrive cache... region=[{}], key=[{}]", region, key);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawKey = rawKey(key);

        byte[] rawValue = run(new JedisCallback<byte[]>() {
            @Override
            public byte[] execute(Jedis jedis) {
                // check cache is expired
                String timestampStr = jedis.hget(getExpireRegion(region), getExpireKey(key));
                if (timestampStr == null || timestampStr.isEmpty())
                    return jedis.hget(rawRegion, rawKey);

                Long timestamp = Long.parseLong(timestampStr);
                if (timestamp != 0 && timestamp <= System.currentTimeMillis()) {
                    // if cache is expired, delete cache
                    jedis.hdel(rawRegion, rawKey);
                    jedis.hdel(getExpireRegion(region), getExpireKey(key));
                    return null;
                }
                return jedis.hget(rawRegion, rawKey);
            }
        });
        Object value = deserializeValue(rawValue);
        log.trace("retrieve cache entity. region=[{}], key=[{}], value=[{}]", region, key, value);
        return value;
    }

    /**
     * retrieve all cached items in specified region
     *
     * @param region region
     * @return collection of cached items
     */
    public Set<Object> keysInRegion(String region) {
        log.trace("retrieve all cached items in region=[{}]", region);

        try {
            final byte[] rawRegion = rawRegion(region);
            Set<byte[]> rawKeys = run(new JedisCallback<Set<byte[]>>() {
                @Override
                public Set<byte[]> execute(Jedis jedis) {
                    return jedis.hkeys(rawRegion);
                }
            });

            if (rawKeys != null && rawKeys.size() > 0) {
                final Set<Object> keys = new HashSet<Object>();
                for (final byte[] rawKey : rawKeys) {
                    final Object key = deserializeKey(rawKey);
                    if (!isExpired(region, key)) {
                        keys.add(key);
                    }
                }
                return keys;
            }
        } catch (Exception ignored) { }
        return new HashSet<Object>();
    }

    /**
     * get cache count in region
     *
     * @param region region
     * @return cache item count in region
     */
    public Long keySizeInRegion(final String region) {
        final byte[] rawRegion = rawRegion(region);
        return run(new JedisCallback<Long>() {
            @Override
            public Long execute(Jedis jedis) {
                return jedis.hlen(rawRegion);
            }
        });
    }

    /**
     * get all cached items in specified region
     *
     * @param region region name
     * @return map of keys and all cached items in specified region
     */
    public Map<Object, Object> hgetAll(String region) {
        log.trace("retrive all keys and cached items in region=[{}]", region);

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
     * multiple get cache items in specified region
     *
     * @param region region name
     * @param keys   cache key collection to retrieve
     * @return cache items
     */
    public List<Object> mget(final String region, final Collection<?> keys) {
        log.trace("retrieve multiple cache items in region... region=[{}], keys=[{}]", region, keys);

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
     * save cache
     *
     * @param region region name
     * @param key    cache key to save
     * @param value  cache value to save
     */
    public void set(String region, Object key, Object value) {
        set(region, key, value, expiryInSeconds, TimeUnit.SECONDS);
    }

    /**
     * save cache item
     *
     * @param region           region name
     * @param key              cache key to save
     * @param value            cache value to save
     * @param timeoutInSeconds expire timeout in seconds
     */
    public void set(String region, Object key, Object value, long timeoutInSeconds) {
        set(region, key, value, timeoutInSeconds, TimeUnit.SECONDS);
    }

    /**
     * save cache item
     *
     * @param region  region name
     * @param key     cache key to save
     * @param value   cache value to save
     * @param timeout expire timeout
     * @param unit    expire timeout unit
     */
    public void set(final String region, final Object key, final Object value, long timeout, TimeUnit unit) {
        log.trace("save cache item region=[{}], key=[{}], timeout=[{}], unit=[{}]",
                  region, key, timeout, unit);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawKey = rawKey(key);
        final byte[] rawValue = rawValue(value);
        final int seconds = (int) unit.toSeconds(timeout);

        runWithTx(new JedisTransactionalCallback() {
            @Override
            public void execute(Transaction tx) {
                tx.hset(rawRegion, rawKey, rawValue);
                if (seconds > 0) {
                    final long score = System.currentTimeMillis() + seconds * 1000;
                    tx.hset(getExpireRegion(region), getExpireKey(key), String.valueOf(score));
                }
            }
        });
    }

    /**
     * delete cache item which is expired in region
     *
     * @param region region name
     */
    public void expire(final String region) {
        // score 가 현재 시각보다 작은 값을 가진 member 를 추려내, 삭제한다.
        try {
            final Set<Object> keys = keysInRegion(region);
            final Set<String> expiredKeys = new HashSet<String>();
            for (Object k : keys) {
                if (isExpired(region, k)) {
                    expiredKeys.add(getExpireKey(k));
                }
            }
            if (expiredKeys.size() > 0) {
                run(new JedisCallback<Long>() {
                    @Override
                    public Long execute(Jedis jedis) {
                        String[] keyArr = new String[expiredKeys.size()];
                        keyArr = expiredKeys.toArray(keyArr);
                        return jedis.hdel(getExpireKey(region), keyArr);
                    }
                });
            }
        } catch (Exception ignored) {
            log.warn("Error in Cache Expiration Method.", ignored);
        }
    }

    /**
     * delete cache item in specified region.
     *
     * @param region region name
     * @param key    cache key to delete
     * @return count of deleted key
     */
    public Long del(final String region, final Object key) {
        log.trace("캐시를 삭제합니다. region=[{}], key=[{}]", region, key);

        final byte[] rawRegion = rawRegion(region);
        final byte[] rawKey = rawKey(key);

        runWithTx(new JedisTransactionalCallback() {
            @Override
            public void execute(Transaction tx) {
                tx.hdel(rawRegion, rawKey);
                tx.hdel(getExpireRegion(region), getExpireKey(key));
            }
        });

        return 1L;
    }

    /**
     * multiplu delete cache items in specified region
     *
     * @param keys key collection to delete
     */
    public void mdel(final String region, final Collection<?> keys) {

        final byte[] rawRegion = rawRegion(region);
        final byte[][] rawKeys = rawKeys(keys);

        final String expireRegion = getExpireRegion(region);
        runWithTx(new JedisTransactionalCallback() {
            @Override
            public void execute(Transaction tx) {
                for (byte[] rawKey : rawKeys) {
                    tx.hdel(rawRegion, rawKey);
                    // tx.zrem(rawZkey, rawKey);
                }
                for (Object key : keys) {
                    tx.hdel(expireRegion, getExpireKey(key));
                }
            }
        });
    }

    /**
     * delete region
     *
     * @param region region name to delete
     */
    public void deleteRegion(final String region) throws CacheException {

        final byte[] rawRegion = rawRegion(region);
        log.debug("delete region region=[{}]", region);

        runWithTx(new JedisTransactionalCallback() {
            @Override
            public void execute(Transaction tx) {
                tx.del(rawRegion);
                tx.del(getExpireRegion(region));
            }
        });
    }

    /**
     * flush db
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
    private byte[] rawKey(final Object key) {
        return getKeySerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    private byte[][] rawKeys(final Collection<?> keys) {
        byte[][] rawKeys = new byte[keys.size()][];
        int i = 0;
        for (Object key : keys) {
            rawKeys[i++] = getKeySerializer().serialize(key);
        }
        return rawKeys;
    }

    /**
     * serialize region name
     */
    private byte[] rawRegion(final String region) {
        return regionSerializer.serialize(region);
    }

    /**
     * deserialize key
     */
    private Object deserializeKey(final byte[] rawKey) {
        return getKeySerializer().deserialize(rawKey);
    }

    /**
     * serialize cache value
     */
    private byte[] rawValue(final Object value) {
        return getValueSerializer().serialize(value);
    }

    /**
     * deserialize raw value
     */
    private Object deserializeValue(final byte[] rawValue) {
        return getValueSerializer().deserialize(rawValue);
    }

    /**
     * execute the specified callback
     */
    private <T> T run(final JedisCallback<T> callback) {

        Jedis jedis = jedisPool.getResource();
        try {
            return callback.execute(jedis);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * execute the specified callback under transaction
     * HINT: https://github.com/xetorthio/jedis/wiki/AdvancedUsage
     *
     * @param callback executable instance under transaction
     */
    private List<Object> runWithTx(final JedisTransactionalCallback callback) {

        Jedis jedis = jedisPool.getResource();
        try {
            Transaction tx = jedis.multi();
            callback.execute(tx);
            return tx.exec();
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * execute the specified callback under Redis Pipeline
     * HINT: https://github.com/xetorthio/jedis/wiki/AdvancedUsage
     *
     * @param callback executable instance unider Pipeline
     */
    private void runWithPipeline(final JedisPipelinedCallback callback) {
        final Jedis jedis = jedisPool.getResource();
        try {
            final Pipeline pipeline = jedis.pipelined();
            callback.execute(pipeline);
            // use #sync(), not #exec()
            pipeline.sync();
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * deserize the specified raw key set.
     *
     * @return original key set.
     */
    private Set<Object> deserializeKeys(final Set<byte[]> rawKeys) {
        return SerializationTool.deserialize(rawKeys, getKeySerializer());
    }

    /**
     * deseriaize the specified raw value collection
     *
     * @return collection of original value
     */
    private List<Object> deserializeValues(final List<byte[]> rawValues) {
        return SerializationTool.deserialize(rawValues, getValueSerializer());
    }
}
