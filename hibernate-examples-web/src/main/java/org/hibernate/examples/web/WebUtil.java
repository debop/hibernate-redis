package org.hibernate.examples.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * kr.hconnect.backend.push.web.WebUtil
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 9. 18. 오전 2:56
 */
public class WebUtil {

    public static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                          MediaType.APPLICATION_JSON.getSubtype(),
                          Charset.forName("utf8"));

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = objectMapper();
        return mapper.writeValueAsBytes(object);
    }

    /**
     * 날짜를 UTC Timestamp 로 표현하기 위해 Jackson 라이브러리를 사용합니다.
     * HINT: http://wiki.fasterxml.com/JacksonFAQDateHandling
     */
    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}
