package org.hibernate.cache.redis.jedis;

import redis.clients.jedis.Jedis;

/**
 * Jedis 를 입력받아 jedis에 대해 작업을 수행합니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 10. 오전 10:22
 */
public interface IJedisCallback<T> {

    /**
     * 작업을 수행합니다.
     *
     * @param jedis
     * @return
     */
    public T execute(Jedis jedis);
}
