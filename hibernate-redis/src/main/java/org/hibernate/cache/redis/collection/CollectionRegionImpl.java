package org.hibernate.cache.redis.collection;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.access.PutFromLoadValidator;
import org.hibernate.cache.redis.impl.BaseTransactionalDataRegion;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import redis.clients.jedis.Jedis;

/**
 * org.hibernate.cache.redis.collection.CollectionRegionImpl
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 1:23
 */
public class CollectionRegionImpl extends BaseTransactionalDataRegion implements CollectionRegion {

    public CollectionRegionImpl(Jedis jedis, String name, CacheDataDescription metadata, RegionFactory factory) {
        super(jedis, name, metadata, factory);
    }

    @Override
    public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        if (AccessType.READ_ONLY.equals(accessType) || AccessType.TRANSACTIONAL.equals(accessType))
            return new TransactionalAccess(this);

        throw new CacheException("Unsupported access type [" + accessType.getExternalName() + "]");
    }

    public PutFromLoadValidator getPutFromLoadValidator() {
        return new PutFromLoadValidator(jedis);
    }
}
