package org.hibernate.examples.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.valueOf;

/**
 * org.hibernate.examples.utils.ToStringHelper
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 4:35
 */
public class ToStringHelper {

  public static ToStringHelper create(Object self) {
    return new ToStringHelper(self.getClass().getSimpleName());
  }

  private Map<String, Object> map = new LinkedHashMap<String, Object>();
  private final String className;

  public ToStringHelper(final String className) {
    this.className = className;
  }

  public ToStringHelper add(String name, Object value) {
    return addMap(name, value);
  }

  public ToStringHelper add(String name, boolean value) {
    return addMap(name, valueOf(value));
  }

  public ToStringHelper add(String name, char value) {
    return addMap(name, valueOf(value));
  }

  public ToStringHelper add(String name, double value) {
    return addMap(name, valueOf(value));
  }

  public ToStringHelper add(String name, float value) {
    return addMap(name, valueOf(value));
  }

  public ToStringHelper add(String name, int value) {
    return addMap(name, valueOf(value));
  }

  public ToStringHelper add(String name, long value) {
    return addMap(name, valueOf(value));
  }

  @Override
  public String toString() {
    boolean isFirst = true;
    final String separator = ",";
    StringBuilder builder = new StringBuilder(32).append(className).append("{");
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      if (!isFirst) {
        builder.append(separator);
      }
      builder.append(entry.getKey()).append("=").append(entry.getValue());
      isFirst = false;
    }
    builder.append("}");
    return builder.toString();
  }

  private ToStringHelper addMap(final String name, Object value) {
    map.put(name, value);
    return this;
  }
}
