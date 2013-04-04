package org.hibernate.cache.redis.collection;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.access.TransactionalAccessDelegate;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;

/**
 * org.hibernate.cache.redis.collection.TransactionalAccess
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 1:23
 */
public class TransactionalAccess implements CollectionRegionAccessStrategy {

    private final CollectionRegionImpl region;
    private final TransactionalAccessDelegate delegate;

    TransactionalAccess(CollectionRegionImpl region) {
        this.region = region;
        this.delegate = new TransactionalAccessDelegate(region, region.getPutFromLoadValidator());
    }

    @Override
    public CollectionRegion getRegion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SoftLock lockRegion() throws CacheException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unlockRegion(SoftLock lock) throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void remove(Object key) throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAll() throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void evict(Object key) throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void evictAll() throws CacheException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
