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

package org.hibernate.stresser.persistence.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SnappyCodec;
import org.redisson.config.Config;
import org.redisson.config.ElasticacheServersConfig;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
@Component
@DependsOn(value = "applicationContextProvider")
public class RedisClientProvider {

  private RedissonClient redisClient;

  private String redisNodes = "localhost:6379";

  @PostConstruct
  public void initialize() {
    Config config = new Config();
    ElasticacheServersConfig clusterConfig = config.useElasticacheServers();
    clusterConfig.setScanInterval(2000);
    clusterConfig.addNodeAddress(StringUtils.tokenizeToStringArray(redisNodes, ",", true, true));
    config.setCodec(new SnappyCodec());
    redisClient = Redisson.create(config);
  }

  @PreDestroy
  public void destroy() {
    if (redisClient != null) {
      redisClient.shutdown();
    }
  }

  public RedissonClient getRedisClient() {
    return redisClient;
  }
}
