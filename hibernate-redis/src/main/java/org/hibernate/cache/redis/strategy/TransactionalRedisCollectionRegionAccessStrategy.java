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

package org.hibernate.cache.redis.strategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cache.redis.regions.RedisCollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * JTA CollectionRegionAccessStrategy.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalRedisCollectionRegionAccessStrategy
		extends AbstractRedisAccessStrategy<RedisCollectionRegion>
		implements CollectionRegionAccessStrategy {

	@Getter
	private final JedisClient jedisClient;

	public TransactionalRedisCollectionRegionAccessStrategy(RedisCollectionRegion region,
	                                                        Settings settings) {
		super(region, settings);
		this.jedisClient = region.getJedisClient();
	}

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		try {
			return jedisClient.get(key);
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	@Override
	public boolean putFromLoad(Object key,
	                           Object value,
	                           long txTimestamp,
	                           Object version,
	                           boolean minimalPutOverride) throws CacheException {
		log.trace("putFromLoad... key=[{}]", key);
		try {
			if (minimalPutOverride && jedisClient.exists(key))
				return false;

			jedisClient.set(key, value);
			return true;
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	@Override
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		return null;
	}

	@Override
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		// nothing to do.
	}

	@Override
	public void remove(Object key) throws CacheException {
		try {
			jedisClient.delete(key);
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}
}
