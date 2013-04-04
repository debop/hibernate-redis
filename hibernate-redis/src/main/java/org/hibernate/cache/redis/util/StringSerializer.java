package org.hibernate.cache.redis.util;

import java.nio.charset.Charset;

/**
 * org.hibernate.cache.redis.util.StringSerializer
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오전 11:05
 */
public class StringSerializer {

    private final Charset charset;

    public StringSerializer() {
        this(Charset.forName("UTF-8"));
    }

    public StringSerializer(Charset charset) {
        assert charset != null;
        this.charset = charset;
    }

    public byte[] serialize(String str) {
        return (str == null) ? null : str.getBytes(charset);
    }

    public String deserialize(byte[] bytes) {
        return (bytes == null) ? null : new String(bytes, charset);
    }
}
