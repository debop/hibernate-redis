package org.hibernate.cache.redis.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates increasing identifiers (in a single VM only).
 * Not valid across multiple VMs. Yet, the identifier is based on time, so that the drifting
 * across a cluster should not ever be large...
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 9:15
 */
public final class Timestamper {
    /**
     * Value for left shifting System.currentTimeMillis, freeing some space for the counter
     */
    public static final int BIN_DIGITS = 12;

    /**
     * What is one milliseconds, based on "counter value reserved space", for this Timestamper
     */
    public static final int ONE_MS = 1 << BIN_DIGITS;
    private static final AtomicLong VALUE = new AtomicLong();

    private Timestamper() { }

    /**
     * Returns an increasing unique value based on the System.currentTimeMillis()
     * with some additional reserved space for a counter.
     *
     * @return uniquely & increasing value
     */
    public static long next() {
        int runs = 0;
        while (true) {
            long base = System.currentTimeMillis() << BIN_DIGITS;
            long maxValue = base + ONE_MS - 1;

            for (long current = VALUE.get(), update = Math.max(base, current + 1); update < maxValue;
                 current = VALUE.get(), update = Math.max(base, current + 1)) {
                if (VALUE.compareAndSet(current, update)) {
                    return update;
                }
            }
            ++runs;
        }
    }
}
