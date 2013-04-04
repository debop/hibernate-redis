package redis.client.serializer;

/**
 * SerializationException
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 10:57
 */
public class SerializationException extends RuntimeException {

    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SerializationException(String msg) {
        super(msg);
    }
}
