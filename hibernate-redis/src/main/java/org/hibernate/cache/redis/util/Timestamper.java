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

package org.hibernate.cache.redis.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates increasing identifiers (in a single VM only).
 * Not valid across multiple VMs. Yet, the identifier is based on time, so that the drifting
 * across a cluster should not ever be large...
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
