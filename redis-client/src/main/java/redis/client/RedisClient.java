package redis.client;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import redis.client.serializer.BinaryRedisSerializer;
import redis.client.serializer.IRedisSerializer;
import redis.client.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;

import java.util.Collection;

/**
 * redis.client.RedisClient
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 10:43
 */
@Slf4j
public class RedisClient<K, V> {

    private static final IRedisSerializer<?> defaultSerializer = new BinaryRedisSerializer();

    @Getter
    private final Jedis jedis;

    @Getter
    @Setter
    private IRedisSerializer keySerializer = null;
    @Getter
    @Setter
    private IRedisSerializer valueSerializer = null;
    @Getter
    @Setter
    private IRedisSerializer<String> stringSerializer = new StringRedisSerializer();

    public RedisClient(Jedis jedis) {
        this.jedis = jedis;
    }


    @SuppressWarnings("unchecked")
    private byte[] rawKey(Object key) {
        assert key != null;
        return keySerializer.serialize(key);
    }

    @SuppressWarnings("unchecked")
    private byte[] rawString(String key) {
        return stringSerializer.serialize(key);
    }

    @SuppressWarnings("unchecked")
    private byte[] rawValue(Object value) {
        return valueSerializer.serialize(value);
    }

    @SuppressWarnings("unchecked")
    private byte[][] rawKeys(Collection<K> keys) {
        final byte[][] rawKeys = new byte[keys.size()][];

        int i = 0;
        for (K key : keys) {
            rawKeys[i++] = rawKey(key);
        }
        return rawKeys;
    }

    public void delete(K key) {
        jedis.del(rawKey(key));
    }

}
