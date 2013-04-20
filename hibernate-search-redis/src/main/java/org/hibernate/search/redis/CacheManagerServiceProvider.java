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

package org.hibernate.search.redis;

import org.hibernate.search.spi.BuildContext;
import org.hibernate.search.spi.ServiceProvider;

import java.util.Properties;

/**
 * org.hibernate.search.redis.CacheManagerServiceProvider
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 3. 오후 8:26
 */
public class CacheManagerServiceProvider implements ServiceProvider<RedisCacheManager> {

    @Override
    public void start(Properties properties, BuildContext context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RedisCacheManager getService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
