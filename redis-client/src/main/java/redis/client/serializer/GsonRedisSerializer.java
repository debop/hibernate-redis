package redis.client.serializer;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * redis.client.serializer.GsonRedisSerializer
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 11:04
 */
@Slf4j
public class GsonRedisSerializer<T> implements IRedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final Class<T> clazz;
    private final Gson gson;

    public GsonRedisSerializer(Class<T> clazz) {
        this(clazz, new Gson());
    }

    public GsonRedisSerializer(Class<T> clazz, Gson gson) {
        this.clazz = clazz;
        this.gson = gson;
    }


    @Override
    public byte[] serialize(T t) throws SerializationException {
        return gson.toJson(t).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        String jsonText = new String(bytes, DEFAULT_CHARSET);
        return gson.fromJson(jsonText, clazz);
    }
}
