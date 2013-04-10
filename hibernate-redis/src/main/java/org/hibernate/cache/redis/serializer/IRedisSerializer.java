package org.hibernate.cache.redis.serializer;

/**
 * Serializer for Redis Key or Value
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 9 오후 10:20
 */
public interface IRedisSerializer<T> {

    /**
     * Serialize Object
     */
    byte[] serialize(T graph);

    /**
     * Deserialize to object
     */
    T deserialize(byte[] bytes);
}
