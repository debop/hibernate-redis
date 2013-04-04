package redis.client.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;

/**
 * redis.client.serializer.JacksonRedisSerializer
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 11:23
 */
public class JacksonRedisSerializer<T> implements IRedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private final Class<T> clazz;
    private ObjectMapper objectMapper = new ObjectMapper();

    public JacksonRedisSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T graph) throws SerializationException {
        if (graph == null)
            return new byte[0];

        try {
            return this.objectMapper.writeValueAsBytes(graph);
        } catch (Exception e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0)
            return null;

        try {
            return (T) this.objectMapper.readValue(bytes, 0, bytes.length, clazz);
        } catch (Exception e) {
            throw new SerializationException("Could not read JSON: " + e.getMessage(), e);
        }
    }
}
