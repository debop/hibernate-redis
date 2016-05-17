package org.hibernate.cache.redis.jedis;

/**
 * Created with IntelliJ IDEA.
 * User: stadia
 * Date: 13. 11. 26
 * Time: 오후 12:02
 */
public class JedisCacheException extends RuntimeException {
  public JedisCacheException(Throwable e) {
    super(e);
  }

  public JedisCacheException(String s) {
    super(s);
  }

  public JedisCacheException(String s, Throwable e) {
    super(s, e);
  }
}
