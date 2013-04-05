package org.hibernate.cache.redis.impl;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisClient;
import org.hibernate.cache.redis.util.RedisTool;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.RegionFactory;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * hibernate region의 기본 클래스입니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오전 10:12
 */
@Slf4j
public abstract class BaseRegion implements Region {

    @Getter
    private final String name;
    @Getter
    private final RegionFactory factory;
    @Getter
    private final TransactionManager tm;
    @Getter
    protected final RedisClient redis;

    private final Object syncObject = new Object();

    private enum InvalidateState {INVALID, CLEARING, VALID}

    private final AtomicReference<InvalidateState> invalidateState =
            new AtomicReference<InvalidateState>(InvalidateState.VALID);

    protected BaseRegion(RedisClient redis, String name, RegionFactory factory) {
        this(redis, name, factory, null);
    }

    protected BaseRegion(RedisClient redis, String name, RegionFactory factory, TransactionManager tm) {
        if (log.isTraceEnabled())
            log.trace("Region을 생성합니다. name=[{}], factory=[{}]", name, factory);
        assert redis != null : "Jedis should not null";

        this.redis = redis;
        this.name = name;
        this.factory = factory;
        this.tm = tm;
    }

    /**
     * 캐시된 항목의 갯수
     *
     * @return
     */
    @Override
    public long getElementCountInMemory() {
        return redis.dbSize();
    }

    /**
     * Not supported.
     *
     * @return -1
     */
    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    /**
     * Not supported.
     *
     * @return -1
     */
    @Override
    public long getSizeInMemory() {
        return -1;
    }

    /**
     * Timeout in seconds
     *
     * @return 60 (seconds)
     */
    @Override
    public int getTimeout() {
        return 60; // 60 seconds;
    }

    @Override
    public long nextTimestamp() {
        return factory.nextTimestamp();
    }

    @Override
    public Map toMap() {
        if (checkValid()) {
            buildMap();
        }
        return Collections.EMPTY_MAP;
    }

    public Map<String, Object> buildMap() {
        Set<String> keys = getKeys();

        List<Object> values = redis.mget(getKeys());

        Map<String, Object> map = Maps.newHashMap();
        int i = 0;
        for (String key : keys) {
            map.put(key, values.get(i++));
        }
        return map;
    }

    @Override
    public void destroy() throws CacheException {
        try {
            invalidateRegion();
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean contains(Object key) {
        return checkValid() && redis.exists((String) key);
    }

    protected boolean isValid() {
        return invalidateState.get() == InvalidateState.VALID;
    }

    public boolean checkValid() {
        boolean valid = isValid();
        if (!valid) {
            synchronized (syncObject) {
                if (invalidateState.compareAndSet(InvalidateState.INVALID, InvalidateState.CLEARING)) {

                    Transaction tx = suspend();
                    try {
                        // clear region in a redis transaction
                        RedisTool.withinTx(redis, new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                invalidateRegion();
                                return null;
                            }
                        });
                        invalidateState.compareAndSet(InvalidateState.CLEARING, InvalidateState.VALID);
                    } catch (Exception e) {
                        if (log.isWarnEnabled())
                            log.warn("Could not invalidate region:" + e.getLocalizedMessage());
                    } finally {
                        resume(tx);
                    }
                }
            }

        }
        return valid;
    }

    /**
     * Tell the TransactionManager to suspend any ongoing transaction.
     *
     * @return the transaction that was suspended, or <code>null</code> if
     *         there wasn't one
     */
    public Transaction suspend() {
        Transaction tx = null;
        try {
            if (tm != null) {
                tx = tm.suspend();
            }
        } catch (SystemException se) {
            throw new CacheException("Could not suspend transaction", se);
        }
        return tx;
    }

    /**
     * Tell the TransactionManager to resume the given transaction
     *
     * @param tx the transaction to suspend. May be <code>null</code>.
     */
    public void resume(Transaction tx) {
        try {
            if (tx != null)
                tm.resume(tx);
        } catch (Exception e) {
            throw new CacheException("Could not resume transaction", e);
        }
    }

    public void invalidateRegion() {
        if (log.isDebugEnabled())
            log.debug("Invalidate region. region name=[{}]", name);

        redis.delete(getKeys());
    }

    public Set<String> getKeys() {
        return redis.keys(name + ":*");
    }

    public boolean isTransactionAware() {
        return tm != null;
    }
}
