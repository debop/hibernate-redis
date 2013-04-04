package redis.client.serializer;


/**
 * redis.client.serializer.IRedisSerializer
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 10:56
 */
public interface IRedisSerializer<T> {

    byte[] serialize(T t) throws SerializationException;

    T deserialize(byte[] bytes) throws SerializationException;
}
