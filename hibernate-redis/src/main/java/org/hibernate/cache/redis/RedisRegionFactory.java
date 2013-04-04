package org.hibernate.cache.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.util.RedisTool;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * hibernate 2nd cache region factory using Redis
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 3. 오후 7:55
 */
@Slf4j
public class RedisRegionFactory implements RegionFactory {

    @Getter
    @Setter
    protected volatile Jedis jedis;
    @Getter
    @Setter
    protected Settings settings;

    @Getter
    private List<String> regionNames = new ArrayList<String>();

    @Getter
    @Setter
    private Integer expireSeconds = 60; // seconds

    protected RedisRegionFactory() {}

    protected RedisRegionFactory(Properties props) {}

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        if (log.isInfoEnabled())
            log.info("Redis region factory를 시작합니다...");

        try {
            expireSeconds = Integer.decode(properties.getProperty("redis.expireTimeout", "60")); // 60 seconds
            jedis = RedisTool.createJedis(properties);
        } catch (CacheException ce) {
            throw ce;
        } catch (Throwable t) {
            throw new CacheException("Redis region factory를 시작하는데 실패했습니다.", t);
        }
        if (log.isInfoEnabled())
            log.info("Redis region factory가 시작했습니다.");
    }

    @Override
    public void stop() {
        if (log.isDebugEnabled())
            log.debug("Redis region factory를 중지합니다.");
        try {
            stopCacheRegions();
            stopCacheManager();
        } catch (Throwable t) {
            log.warn("Redis region factory를 중지하는데 문제가 생겼습니다.", t);
        }
    }

    protected void stopCacheRegions() {
        if (log.isDebugEnabled())
            log.debug("Redis DB의 내용을 모두 삭제합니다...");
        jedis.flushDB();
    }

    protected void stopCacheManager() {
        if (log.isDebugEnabled())
            log.debug("Stop Cache Manager...");

        if (jedis != null && jedis.isConnected()) {
            jedis.disconnect();
            jedis = null;
        }
    }

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

    @Override
    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
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
