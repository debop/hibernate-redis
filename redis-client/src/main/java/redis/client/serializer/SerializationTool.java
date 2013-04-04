package redis.client.serializer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * redis.client.serializer.SerializationTool
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 11:38
 */
public abstract class SerializationTool {

    static final byte[] EMPTY_ARRAY = new byte[0];

    static boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    @SuppressWarnings("unchecked")
    static <T extends Collection<?>> T deserializeValues(Collection<byte[]> rawValues,
                                                         Class<T> clazz,
                                                         IRedisSerializer<?> redisSerializer) {

        if (rawValues == null)
            return null;

        int valueCount = rawValues.size();
        Collection<Object> values =
                List.class.isAssignableFrom(clazz)
                        ? Lists.newArrayListWithCapacity(valueCount)
                        : Sets.newLinkedHashSetWithExpectedSize(valueCount);

        for (byte[] bs : rawValues) {
            values.add(redisSerializer.deserialize(bs));
        }
        return (T) values;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> deserialize(Set<byte[]> rawValues, IRedisSerializer<T> redisSerializer) {
        return deserializeValues(rawValues, Set.class, redisSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> deserialize(List<byte[]> rawValues, IRedisSerializer<T> redisSerializer) {
        return deserializeValues(rawValues, List.class, redisSerializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> deserialize(Collection<byte[]> rawValues, IRedisSerializer<T> redisSerializer) {
        return deserializeValues(rawValues, List.class, redisSerializer);
    }
}
