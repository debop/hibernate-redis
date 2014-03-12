package org.hibernate.cache.redis.jedis;

import redis.clients.jedis.Pipeline;

/**
 * Execute method using Redis {@link redis.clients.jedis.Pipeline}
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2013. 11. 16. 오후 1:46
 */
public interface JedisPipelinedCallback {

    /**
     * execute using Redis Pipeline.
     *
     * @param pipeline Jedis Pipeline
     */
    void execute(final Pipeline pipeline);
}
