package org.hibernate.cache.redis.serializer;

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Serializer using Fast-Serialization
 *
 * @author Sunghyouk Bae
 * @deprecated Error in Tomcat to serialize/deserialize Float type
 */
@Slf4j
@Deprecated
public class FstRedisSerializer<T> implements RedisSerializer<T> {

    private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    @Override
    public byte[] serialize(final T graph) {
        if (graph == null)
            return EMPTY_BYTES;

        try {
            @Cleanup
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FSTObjectOutput oos = conf.getObjectOutput(os);
            oos.writeObject(graph);
            oos.flush();

            return os.toByteArray();
        } catch (Exception e) {
            log.warn("Fail to serializer graph. graph=" + graph, e);
            return EMPTY_BYTES;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(final byte[] bytes) {
        if (SerializationTool.isEmpty(bytes))
            return null;

        try {
            @Cleanup
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            FSTObjectInput ois = conf.getObjectInput(is);
            return (T) ois.readObject();
        } catch (Exception e) {
            log.warn("Fail to deserialize bytes.", e);
            return null;
        }
    }
}
