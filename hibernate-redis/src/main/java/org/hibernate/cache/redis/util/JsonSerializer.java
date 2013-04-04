package org.hibernate.cache.redis.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * org.hibernate.cache.redis.util.JsonSerializer
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 2:50
 */
@Slf4j
public class JsonSerializer {
    @Getter
    private final ObjectMapper mapper;

    public JsonSerializer() {
        this(new ObjectMapper());
    }

    public JsonSerializer(ObjectMapper mapper) {

        this.mapper = (mapper != null) ? mapper : new ObjectMapper();
    }

    public byte[] serialize(Object graph) {
        try {
            if (graph == null)
                return new byte[0];

            if (log.isDebugEnabled())
                log.debug("인스턴스를 JSON 포맷으로 직렬화합니다. graph=[{}]", graph);

            return mapper.writeValueAsBytes(graph);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("객체를 Json 직렬화하는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }

    public String serializeToText(Object graph) {
        try {
            if (graph == null)
                return "";

            if (log.isDebugEnabled())
                log.debug("인스턴스를 JSON 포맷으로 직렬화합니다. graph=[{}]", graph);

            return mapper.writeValueAsString(graph);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("객체를 Json 직렬화하는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T deserializeFromText(String jsonText, Class<T> targetType) {
        try {
            if (jsonText == null || jsonText.length() == 0)
                return (T) null;

            if (log.isDebugEnabled())
                log.debug("JSON 역직렬화를 수행합니다. valueType=[{}]", targetType.getName());

            return mapper.readValue(jsonText, targetType);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("Json 역직렬화하는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T deserialize(byte[] bytes, Class<T> targetType) {
        try {
            if (bytes == null || bytes.length == 0)
                return (T) null;

            if (log.isDebugEnabled())
                log.debug("JSON 역직렬화를 수행합니다. targetType=[{}]", targetType.getName());

            return mapper.readValue(bytes, targetType);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("Json 역직렬화하는데 실패했습니다.", e);
            throw new RuntimeException(e);
        }
    }
}
