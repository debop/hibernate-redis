package org.hibernate.cache.redis;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * org.hibernate.cache.redis.AbstractRegionFactory
 * User: sunghyouk.bae@gmail.com
 * Date: 12. 9. 23.
 */
public abstract class AbstractRegionFactory implements RegionFactory {

	@Override
	public void start(Settings settings, Properties properties) throws CacheException {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void stop() {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean isMinimalPutsEnabledByDefault() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public AccessType getDefaultAccessType() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public long nextTimestamp() {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata)
		throws CacheException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata)
		throws CacheException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata)
		throws CacheException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
