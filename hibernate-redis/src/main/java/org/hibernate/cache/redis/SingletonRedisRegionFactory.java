package org.hibernate.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.util.RedisTool;
import org.hibernate.cfg.Settings;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A singleton RedisRegionFactory implementation.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:31
 */
@Slf4j
public class SingletonRedisRegionFactory extends AbstractRedisRegionFactory {

    private static final AtomicInteger ReferenceCount = new AtomicInteger();

    private RedisClient redis;

    public SingletonRedisRegionFactory(Properties props) {
        super(props);
        this.redis = RedisTool.createRedisClient(props);
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        if (log.isDebugEnabled())
            log.debug("Start region factory");

        this.settings = settings;

        try {
            this.redis = RedisTool.createRedisClient(props);
            ReferenceCount.incrementAndGet();
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void stop() {
        if (log.isDebugEnabled())
            log.debug("Stop regoin factory");

        try {
            if (ReferenceCount.decrementAndGet() == 0) {
                redis.flushDb();
            }
            redis = null;
        } catch (Exception e) {
            log.error("redis region factory fail to stop.", e);
        }
    }
}
