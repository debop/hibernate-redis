package redis.client.serializer;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * {@link ObjectOutputStream}, {@link ObjectInputStream} 을 이용하여 객체를 직렬화/역직렬화 합니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 11:00
 */
@Slf4j
public class BinaryRedisSerializer implements IRedisSerializer<Object> {

    @Override
    public byte[] serialize(Object graph) throws SerializationException {
        if (graph == null)
            return new byte[0];

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(graph);
            oos.flush();

            return bos.toByteArray();
        } catch (Exception e) {
            BinaryRedisSerializer.log.error("객체정보를 직렬화하는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0)
            return null;

        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        } catch (Exception e) {
            BinaryRedisSerializer.log.error("객체정보를 역직렬화하는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }
}
