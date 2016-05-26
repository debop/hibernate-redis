package org.hibernate.examples.utils;

/**
 * org.hibernate.examples.utils.Serializers
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 5:33
 */
public final class Serializers {

  private Serializers() {
  }

  private static final BinarySerializer serializer = new BinarySerializer();

  /**
   * 객체를 바이너리 직렬화를 수행합니다.
   *
   * @param graph 직렬화할 객체
   * @return 직렬화 결과를 담은 바이트 배열
   */
  public static byte[] serializeObject(Object graph) {
    return serializer.serialize(graph);
  }

  /**
   * 직렬화 정보를 담은 바이트 배열을 역직렬화하여 객체를 빌드합니다.
   *
   * @param clazz Type of deserialized object
   * @param <T>   Type of deseiralized object
   * @return deserialized object
   */
  public static <T> T deserializeObject(byte[] bytes, Class<T> clazz) {
    return serializer.deserialize(bytes, clazz);
  }

  /**
   * Binary serializer 를 이용하여 객체를 복사한다.
   *
   * @param graph cobject to copied
   * @param <T>   generic type
   * @return cpoied object
   */
  @SuppressWarnings("unchecked")
  public static <T> T copyObject(T graph) {
    return copyObject(graph, (Class<T>) graph.getClass());
  }

  /**
   * Binary serializer를 이용하여 객체를 복사한다.
   *
   * @param graph object to copied
   * @param clazz Class of copied  object
   * @param <T>   Generic type
   * @return copied object
   */
  public static <T> T copyObject(T graph, Class<T> clazz) {
    return (graph == null) ? null : deserializeObject(serializeObject(graph), clazz);
  }
}
