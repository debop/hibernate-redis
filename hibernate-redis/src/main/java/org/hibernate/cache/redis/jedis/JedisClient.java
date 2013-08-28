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
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.serializer.BinaryRedisSerializer;
import org.hibernate.cache.redis.serializer.RedisSerializer;
import org.hibernate.cache.redis.serializer.SerializationTool;
import org.hibernate.cache.redis.util.CollectionUtil;
import org.hibernate.cache.spi.CacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * RedisClient implements using Jedis library
 * <p/>
 * 참고 : https://github.com/xetorthio/jedis/wiki/AdvancedUsage
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 13. 4. 9 오후 10:20
 */
public class JedisClient {

	private static final Logger log = LoggerFactory.getLogger(JedisClient.class);
	private static final boolean isTraceEnabled = log.isTraceEnabled();
	private static final boolean isDebugEnabled = log.isDebugEnabled();

	public static final int DEFAULT_EXPIRY_IN_SECONDS = 120;
	public static final String DEFAULT_REGION_NAME = "hibernate";

	@Getter
	private final String regionName;

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
	private RedisSerializer keySerializer = new BinaryRedisSerializer<Object>();

	@Getter
	@Setter
	private RedisSerializer valueSerializer = new BinaryRedisSerializer<Object>();

	public JedisClient() {
		this(DEFAULT_REGION_NAME, new JedisPool("localhost"));
	}

	public JedisClient(JedisPool jedisPool) {
		this(DEFAULT_REGION_NAME, jedisPool);
	}

	public JedisClient(String regionName, JedisPool jedisPool) {
		this(regionName, jedisPool, DEFAULT_EXPIRY_IN_SECONDS);
	}

	public JedisClient(String regionName, JedisPool jedisPool, int expiryInSeconds) {
		log.debug("JedisClient created. regionName=[{}], jedisPool=[{}], expiryInSeconds=[{}]",
		          regionName, jedisPool, expiryInSeconds);

		this.regionName = regionName;
		this.jedisPool = jedisPool;
		this.expiryInSeconds = expiryInSeconds;
	}

	/** 서버와의 통신 테스트, "PONG" 을 반환한다 */
	public String ping() {
		return run(new JedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.ping();
			}
		});
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
		final byte[] rawRegion = rawRegion(key);
		final byte[] rawKey = rawValue(key);
		Long rank = run(new JedisCallback<Long>() {
			@Override
			public Long execute(Jedis jedis) {
				return jedis.zrank(rawRegion, rawKey);
			}
		});
		if (isTraceEnabled)
			log.trace("캐시 값이 존재하는지 확인합니다. key=[{}], exists=[{}]", key, (rank != null));
		return rank != null;
	}

	/**
	 * 키에 해당하는 캐시 값을 구합니다.
	 *
	 * @param key 캐시 키
	 * @return 저장된 캐시 값, 없으면 null을 반환한다.
	 */
	public Object get(Object key) {
		if (isTraceEnabled) log.trace("캐시 값을 조회합니다... key=[{}]", key);

		final byte[] rawKey = rawKey(key);
		byte[] rawValue = run(new JedisCallback<byte[]>() {
			@Override
			public byte[] execute(Jedis jedis) {
				return jedis.get(rawKey);
			}
		});
		Object value = deserializeValue(rawValue);

		if (isTraceEnabled) log.trace("캐시 값을 조회했습니다. key=[{}], value=[{}]", key, value);
		return value;
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
	public List mget(Collection<?> keys) {
		if (isTraceEnabled) {
			log.trace("multi get... keys=[{}]", CollectionUtil.toString(keys));
		}

		if (keys == null || keys.size() == 0) {
			return new ArrayList();
		}

		final byte[][] rawKeys = rawKeys(keys);
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
	public final void set(Object key, Object value) {
		set(key, value, -1);
	}

	/**
	 * 캐시를 저장합니다.
	 *
	 * @param key              캐시 키
	 * @param value            캐시 값
	 * @param timeoutInSeconds 유효 기간 (Seconds 단위)
	 */
	public final void set(Object key, Object value, long timeoutInSeconds) {
		set(key, value, timeoutInSeconds, TimeUnit.SECONDS);
	}

	/**
	 * 캐시를 저장합니다.
	 *
	 * @param key     캐시 키
	 * @param value   캐시 값
	 * @param timeout 캐시 유효 시간
	 * @param unit    시간 단위 (기본은 seconds)
	 */
	protected void set(Object key, Object value, long timeout, TimeUnit unit) {
		if (isTraceEnabled)
			log.trace("캐시를 저장합니다... key=[{}], value=[{}]", key, value);

		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(value);
		final byte[] rawRegion = rawRegion(key);
		final int seconds = (int) unit.toSeconds(timeout);

		runWithTx(new JedisTransactionalCallback() {
			@Override
			public void execute(Transaction tx) {
				// TODO: setex 를 사용하면 좋은데, zaddex 가 없다. 향후 jedis에서 psetex 명령어 (timeout을 millis 단위로 설정)를 지원하면, 변경한다.
				tx.set(rawKey, rawValue);
				tx.zadd(rawRegion, 0, rawKey);
				if (seconds > 0) {
					tx.expire(rawKey, seconds);
					tx.expire(rawRegion, seconds);
				}
			}
		});
	}

	public void delete(Object key) {
		del(key);
	}

	/** 지정된 키의 항목으로 삭제합니다. */
	public void del(Object key) {
		if (isTraceEnabled) log.trace("캐시를 삭제합니다. key=[{}]", key);

		final byte[] rawKey = rawKey(key);
		final byte[] rawRegion = rawRegion(key);

		runWithTx(new JedisTransactionalCallback() {
			@Override
			public void execute(Transaction tx) {
				tx.del(rawKey);
				tx.zrem(rawRegion, rawKey);
			}
		});
	}

	/** 지정된 키의 항목으로 삭제합니다. */
	public void mdel(Collection<?> keys) {
		if (isTraceEnabled) log.trace("캐시를 삭제합니다. keys=[{}]", CollectionUtil.toString(keys));
		if (keys == null || keys.size() == 0) return;

		final byte[][] rawKeys = rawKeys(keys);
		final byte[][] rawRegions = rawRegions(keys);
		runWithTx(new JedisTransactionalCallback() {
			@Override
			public void execute(Transaction tx) {
				tx.del(rawKeys);
				for (int i = 0; i < rawKeys.length; i++)
					tx.zrem(rawRegions[i], rawKeys[i]);
			}
		});
	}

	/** 해당 영역의 모든 캐시 항목을 삭제합니다. */
	public void deleteRegion(final String regionName) throws CacheException {
		if (isDebugEnabled)
			log.debug("Region 전체를 삭제합니다... regionName=[{}]", regionName);

		// Redis에서 한 Transaction 하에서 get / set 을 동시에 실행 할 수 없습니다. (복합 함수인 setnx 같은 것을 제외하고)
		//

		try {
			final byte[] rawRegion = rawRegion(regionName);

			if (isTraceEnabled)
				log.trace("영역의 모든 키를 조회합니다... region=[{}]", regionName);

			Set<byte[]> keySet = run(new JedisCallback<Set<byte[]>>() {
				@Override
				public Set<byte[]> execute(Jedis jedis) {
					return jedis.zrange(rawRegion, 0, -1);
				}
			});
			if (keySet.size() > 0) {
				if (isTraceEnabled)
					log.trace("영역의 모든 키를 삭제합니다... key 갯수=[{}]", keySet.size());

				final byte[][] rawKeys = keySet.toArray(new byte[keySet.size()][]);
				runWithTx(new JedisTransactionalCallback() {
					@Override
					public void execute(Transaction tx) {
						tx.del(rawKeys);
						tx.zremrangeByRank(rawRegion, 0, -1);
					}
				});
			}
		} catch (Throwable t) {
			log.error("Region을 삭제하는데 실패했습니다.", t);
			throw new CacheException(t);
		}
	}

	/** DB의 모든 내용을 삭제합니다. */
	public String flushDb() {
		log.info("Redis DB 전체를 flush 합니다...");

		return run(new JedisCallback<String>() {
			@Override
			public String execute(Jedis jedis) {
				return jedis.flushDB();
			}
		});
	}

	/** 키를 byte[] 로 직렬화합니다 * */
	@SuppressWarnings("unchecked")
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

	/** 키를 이용해 region 값을 직렬화합니다. */
	@SuppressWarnings("unchecked")
	private byte[] rawRegion(Object key) {
		return getKeySerializer().serialize(getEntityName(key));
	}

	/** 키를 이용해 raw region 값을 빌드합니다. */
	@SuppressWarnings("unchecked")
	private byte[][] rawRegions(Collection<?> keys) {
		byte[][] rawRegions = new byte[keys.size()][];
		int i = 0;
		for (Object key : keys) {
			rawRegions[i++] = getKeySerializer().serialize(getEntityName(key));
		}
		return rawRegions;
	}

	/** hibernate cache key인 경우 region name을 추출합니다. */
	private String getEntityName(Object key) {
		if (key instanceof CacheKey)
			return ((CacheKey) key).getEntityOrRoleName();
		return regionName;
	}

	/** byte[] 를 key 값으로 역직렬화 합니다 */
	private Object deserializeKey(byte[] rawKey) {
		return getKeySerializer().deserialize(rawKey);
	}

	/** 캐시 값을 byte[]로 직렬화를 수행합니다. */
	@SuppressWarnings("unchecked")
	private byte[] rawValue(Object value) {
		return getValueSerializer().serialize(value);
	}

	/** byte[] 를 역직렬화하여 원 객체로 변환합니다. */
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
		} catch (Throwable t) {
			log.error("Redis 작업 중 예외가 발생했습니다.", t);
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			return (T) null;
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
		} catch (Throwable t) {
			log.error("Redis 작업 중 예외가 발생했습니다.", t);
			jedisPool.returnBrokenResource(jedis);
			jedis = null;
			return null;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	/** Raw Key 값들을 역직렬화하여 Key Set을 반환합니다. */
	@SuppressWarnings("unchecked")
	private Set<Object> deserializeKeys(Set<byte[]> rawKeys) {
		return SerializationTool.deserialize(rawKeys, getKeySerializer());
	}

	/** Raw Value 값들을 역직렬화하여 Value List를 반환합니다. */
	@SuppressWarnings("unchecked")
	private List<Object> deserializeValues(List<byte[]> rawValues) {
		return SerializationTool.deserialize(rawValues, getValueSerializer());
	}
}
