package org.hibernate.cache.redis.util;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public interface CacheTimestamper {
    long next();
}
