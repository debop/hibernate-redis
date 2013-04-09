package org.hibernate.cache.redis.serializer;

/**
 * Serializer for Redis Key or Value
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 9 오후 10:20
 */
public class BinaryRedisSerializer<T> implements IRedisSerializer<T> {

    @Override
    public byte[] serialize(T graph) throws Exception {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public T deserialize(byte[] bytes) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
