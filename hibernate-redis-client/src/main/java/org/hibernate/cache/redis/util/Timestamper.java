/*
 * Copyright (c) 2017. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.cache.redis.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates increasing identifiers (in a single VM only).
 * Not valid across multiple VMs. Yet, the identifier is based on time, so that the drifting
 * across a cluster should not ever be large...
 */
public final class Timestamper implements CacheTimestamper {

  private final AtomicLong VALUE = new AtomicLong();
  private final int BIN_DIGITS = 12;
  private final short ONE_MS = 1 << BIN_DIGITS;

  @Override
  public long next() {
    while (true) {
      long base = System.currentTimeMillis() << BIN_DIGITS;
      long maxValue = base + ONE_MS - 1;

      for (long current = VALUE.get(), update = Math.max(base, current + 1); update < maxValue;
           current = VALUE.get(), update = Math.max(base, current + 1)) {
        if (VALUE.compareAndSet(current, update)) {
          return update;
        }
      }
    }
    }
}

