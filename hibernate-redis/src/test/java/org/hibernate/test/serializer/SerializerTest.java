package org.hibernate.test.serializer;

import org.hibernate.cache.redis.serializer.BinaryRedisSerializer;
import org.hibernate.cache.redis.serializer.FstRedisSerializer;
import org.hibernate.cache.redis.serializer.RedisSerializer;
import org.hibernate.cache.redis.serializer.SnappyRedisSerializer;
import org.hibernate.test.domain.Event;
import org.hibernate.test.domain.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

/**
 * SerializerTest
 *
 * @author Sunghyouk Bae
 */
public class SerializerTest {

    private RedisSerializer<Object> binary = new BinaryRedisSerializer<Object>();
    private RedisSerializer<Object> binSnappy = new SnappyRedisSerializer<Object>(new BinaryRedisSerializer<Object>());
    private RedisSerializer<Object> fst = new FstRedisSerializer<Object>();
    private RedisSerializer<Object> fstSnappy = new SnappyRedisSerializer<Object>(new FstRedisSerializer<Object>());

    private Person smallPerson;
    private Person largePerson;

    @Before
    public void before() {
        smallPerson = new Person();
        smallPerson.setLastname("Bae");
        smallPerson.setFirstname("Sunghyouk");

        largePerson = new Person();
        largePerson.setLastname("Bae");
        largePerson.setFirstname("Sunghyouk");
        for (int x = 0; x < 1000; x++) {
            Event event = new Event();
            event.setTitle("Event Title - " + x);
            event.setDate(new Date());
            largePerson.getEvents().add(event);
            event.setOrganizer(largePerson);
        }
    }

    @Test
    public void binary_serializer_benchmark() {
        final Person small = smallPerson;
        final Person large = largePerson;

        Runnable smallAction = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = binary.serialize(small);
                Person person = (Person) binary.deserialize(bytes);
                assertThat(person).isEqualTo(small);
            }
        };

        Runnable largeAction = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = binary.serialize(large);
                Person person = (Person) binary.deserialize(bytes);
                assertThat(person).isEqualTo(large);
            }
        };

        stopwatch("binary builtin warm-up", 1, smallAction);
        stopwatch("binary builtin warm-up", 1, largeAction);

        stopwatch("binary builtin small", 100, smallAction);
        stopwatch("binary builtin large", 100, largeAction);
    }


    @Test
    public void snappy_serializer_benchmark() {
        final Person small = smallPerson;
        final Person large = largePerson;

        Runnable smallAction = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = binSnappy.serialize(small);
                Person person = (Person) binSnappy.deserialize(bytes);
                assertThat(person).isEqualTo(small);
            }
        };

        Runnable largeAction = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = binSnappy.serialize(large);
                Person person = (Person) binSnappy.deserialize(bytes);
                assertThat(person).isEqualTo(large);
            }
        };

        stopwatch("binary snappy warm-up", 1, smallAction);
        stopwatch("binary snappy warm-up", 1, largeAction);

        stopwatch("binary snappy small", 100, smallAction);
        stopwatch("binary snappy large", 100, largeAction);
    }

    @Test
    public void fst_serializer_benchmark() {
        final Person small = smallPerson;
        final Person large = largePerson;

        Runnable smallAction = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = fst.serialize(small);
                Person person = (Person) fst.deserialize(bytes);
                assertThat(person).isEqualTo(small);
            }
        };

        Runnable largeAction = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = fst.serialize(large);
                Person person = (Person) fst.deserialize(bytes);
                assertThat(person).isEqualTo(large);

            }
        };

        stopwatch("fst warm-up", 1, smallAction);
        stopwatch("fst warm-up", 1, largeAction);

        stopwatch("fst small", 100, smallAction);
        stopwatch("fst large", 100, largeAction);
    }

    @Test
    public void fstSnappy_serializer_benchmark() {
        final Person small = smallPerson;
        final Person large = largePerson;

        Runnable smallAction = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = fstSnappy.serialize(small);
                Person person = (Person) fstSnappy.deserialize(bytes);
                assertThat(person).isEqualTo(small);
            }
        };

        Runnable largeAction = new Runnable() {
            @Override
            public void run() {
                byte[] bytes = fstSnappy.serialize(large);
                Person person = (Person) fstSnappy.deserialize(bytes);
                assertThat(person).isEqualTo(large);
            }
        };

        stopwatch("fst snappy warm-up", 1, smallAction);
        stopwatch("fst snappy warm-up", 1, largeAction);

        stopwatch("fst snappy small", 100, smallAction);
        stopwatch("fst snappy large", 100, largeAction);
    }


    private void stopwatch(String title, int count, Runnable runnable) {

        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            runnable.run();
        }
        long elapsed = System.nanoTime() - start;
        System.out.println(String.format("title=%s : %s", title, (elapsed / 1000)));

    }
}
