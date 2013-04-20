/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.redis.serializer;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZip Serializer
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오전 11:52
 */
@Slf4j
public class GzipRedisSerializer<T> implements RedisSerializer<T> {

    private RedisSerializer<T> innerSerializer;

    public GzipRedisSerializer(RedisSerializer<T> innerSerializer) {
        this.innerSerializer = innerSerializer;
    }

    public static final int BUFFER_SIZE = 4096;

    @Override
    public byte[] serialize(T graph) throws SerializationException {

        if (log.isTraceEnabled())
            log.trace("객체를 직렬화 후에 압축을 수행합니다...");

        if (graph == null)
            return new byte[0];

        try {
            byte[] bytes = innerSerializer.serialize(graph);
            @Cleanup ByteArrayOutputStream bos = new ByteArrayOutputStream();
            @Cleanup GZIPOutputStream gzip = new GZIPOutputStream(bos);

            gzip.write(bytes);
            gzip.close();
            byte[] result = bos.toByteArray();

            if (log.isTraceEnabled())
                log.trace("객체를 직렬화 후에 압축했습니다. 기존=[{}], 압축후=[{}]", bytes.length, result.length);

            return result;
        } catch (Exception e) {
            throw new SerializationException("Gzip 압축 실패", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {

        if (log.isTraceEnabled())
            log.trace("압축된 데이터를 복원 후 객체로 역직렬화 합니다...");

        if (bytes == null || bytes.length == 0)
            return null;

        try {
            @Cleanup ByteArrayOutputStream bos = new ByteArrayOutputStream();
            @Cleanup ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            @Cleanup GZIPInputStream gzip = new GZIPInputStream(bis);

            byte[] buff = new byte[BUFFER_SIZE];
            int n;
            while ((n = gzip.read(buff, 0, BUFFER_SIZE)) > 0) {
                bos.write(buff, 0, n);
            }
            T result = (T) innerSerializer.deserialize(bos.toByteArray());

            if (log.isTraceEnabled())
                log.trace("압축된 데이터를 복원 후 객체로 역직렬화 했습니다. result=[{}]", result);

            return result;
        } catch (Exception e) {
            throw new SerializationException("Gzip 복원 실패", e);
        }
    }
}
