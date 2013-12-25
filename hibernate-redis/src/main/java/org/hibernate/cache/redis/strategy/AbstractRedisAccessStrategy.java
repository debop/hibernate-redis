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

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.regions.RedisTransactionalDataRegion;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * AbstractRedisAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 10:07
 */
@Slf4j
abstract class AbstractRedisAccessStrategy<T extends RedisTransactionalDataRegion> {

    protected final T region;
    protected final Settings settings;

    AbstractRedisAccessStrategy(T region, Settings settings) {
        this.region = region;
        this.settings = settings;
    }

    protected Settings settings() {
        return settings;
    }

    public final boolean putFromLoad(Object key,
                                     Object value,
                                     long txTimestamp,
                                     Object version) {
        return putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
    }

    public abstract boolean putFromLoad(Object key,
                                        Object value,
                                        long txTimestamp,
                                        Object version,
                                        boolean minimalPutOverride);

    /**
     * Region locks are not supported
     */
    public final SoftLock lockRegion() {
        return null;
    }

    /**
     * Region locks are not supported - perform a cache clear as a precaution.
     *
     * @param lock soft lock instance
     * @
     */
    public final void unlockRegion(SoftLock lock) {
        log.trace("clear region... region=[{}]", region.getName());
        region.clear();
    }

    /**
     * A no-op since this is an asynchronous cache access strategy.
     *
     * @param key key
     * @
     */
    public void remove(Object key) { }

    /**
     * Called to evict data from the entire region
     *
     * @
     */
    public final void removeAll() {
        log.trace("remove all cache items...");
        region.clear();
    }

    /**
     * Remove the given mapping without regard to transactional safety
     *
     * @param key key
     * @
     */
    public final void evict(Object key) {
        log.trace("evict cache item... key=[{}]", key);
        region.remove(key);
    }

    /**
     * Remove all mappings without regard to transactional safety
     *
     * @
     */
    public final void evictAll() {
        log.trace("evict all cache items... region=[{}]", region.getName());
        region.clear();
    }
}
