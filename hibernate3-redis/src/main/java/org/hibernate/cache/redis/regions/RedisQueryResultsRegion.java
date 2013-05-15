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

package org.hibernate.cache.redis.regions;

import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cache.redis.strategy.IRedisAccessStrategyFactory;
import org.hibernate.cache.QueryResultsRegion;

import java.util.Properties;

/**
 * A query results region specific wrapper around an Redis instance.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:55
 */
public class RedisQueryResultsRegion extends RedisGeneralDataRegion implements QueryResultsRegion {

    public RedisQueryResultsRegion(IRedisAccessStrategyFactory accessStrategyFactory,
                                   JedisClient jedisClient,
                                   String regionName,
                                   Properties props) {
        super(accessStrategyFactory, jedisClient, regionName, props);
    }
}
