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

/**
 * Generates increasing identifiers (in a single VM only).
 * Not valid across multiple VMs. Yet, the identifier is based on time, so that the drifting
 * across a cluster should not ever be large...
 */
public final class Timestamper {

    private Timestamper() { }

    /**
     * Returns an increasing unique value based on the System.currentTimeMillis()
     * with some additional reserved space for a counter.
     *
     * @return uniquely & increasing value
     */
    public static long next() {
        return System.currentTimeMillis();
    }
}

