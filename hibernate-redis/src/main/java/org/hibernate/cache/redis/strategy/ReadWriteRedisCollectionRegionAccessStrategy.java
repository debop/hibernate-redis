package org.hibernate.cache.redis.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.redis.regions.RedisCollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

/**
 * ReadWriteRedisCollectionRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:13
 */
@Slf4j
public class ReadWriteRedisCollectionRegionAccessStrategy
        extends AbstractReadWriteRedisAccessStrategy<RedisCollectionRegion>
        implements CollectionRegionAccessStrategy {

    public ReadWriteRedisCollectionRegionAccessStrategy(RedisCollectionRegion region, Settings settings) {
        super(region, settings);
    }
}
