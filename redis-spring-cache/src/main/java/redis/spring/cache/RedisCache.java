package redis.spring.cache;

import org.springframework.cache.Cache;

/**
 * redis.spring.cache.RedisCache
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 10:54
 */
public class RedisCache implements Cache {
    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getNativeCache() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ValueWrapper get(Object key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(Object key, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void evict(Object key) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
