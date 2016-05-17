package org.hibernate.examples.utils;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * org.hibernate.examples.utils.BinarySerializer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 5:34
 */
@Slf4j
public class BinarySerializer {

  public byte[] serialize(Object graph) {
    if (graph == null)
      return ArrayUtils.EMPTY_BYTE_ARRAY;

    try {
      @Cleanup ByteArrayOutputStream bos = new ByteArrayOutputStream();
      @Cleanup ObjectOutputStream oos = new ObjectOutputStream(bos);

      oos.writeObject(graph);
      oos.flush();

      return bos.toByteArray();
    } catch (Exception e) {
      log.error("객체 직렬화에 실패했습니다. graph=" + graph, e);
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T deserialize(byte[] bytes, Class<T> clazz) {
    if (ArrayUtils.isEmpty(bytes))
      return (T) null;

    try {
      @Cleanup ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
      @Cleanup ObjectInputStream ois = new ObjectInputStream(bis);
      return (T) ois.readObject();
    } catch (Exception e) {
      log.error("객체 역직렬화에 실패했습니다.", e);
      throw new RuntimeException(e);
    }
  }
}
