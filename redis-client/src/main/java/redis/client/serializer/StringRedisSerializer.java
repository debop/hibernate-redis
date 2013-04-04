package redis.client.serializer;

import java.nio.charset.Charset;

/**
 * redis.client.serializer.StringRedisSerializer
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 11:24
 */
public class StringRedisSerializer implements IRedisSerializer<String> {

    private final Charset charset;

    public StringRedisSerializer() {
        this(Charset.forName("UTF8"));
    }

    public StringRedisSerializer(Charset charset) {
        assert charset != null;
        this.charset = charset;
    }

    @Override
    public byte[] serialize(String s) throws SerializationException {
        return (s == null) ? null : s.getBytes(charset);
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        return (bytes == null) ? null : new String(bytes, charset);
    }
}
