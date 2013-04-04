package org.hibernate.cache.redis.entity;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.RedisRegionFactory;
import org.hibernate.cache.redis.impl.BaseTransactionalDataRegion;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import redis.clients.jedis.Jedis;

/**
 * 엔티티를 저장하기 위한 클래스입니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오전 11:40
 */
public class EntityRegionImpl extends BaseTransactionalDataRegion implements EntityRegion {

    public EntityRegionImpl(Jedis jedis, String name, CacheDataDescription metadata, RedisRegionFactory factory) {
        super(jedis, name, metadata, factory);
    }

    @Override
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
