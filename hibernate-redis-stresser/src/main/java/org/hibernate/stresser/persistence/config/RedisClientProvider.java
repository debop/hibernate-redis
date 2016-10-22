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
        //config.setThreads(4);
        //config.setNettyThreads(4);
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
