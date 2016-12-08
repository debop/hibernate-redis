hibernate-redis  
===============

(About this fork): Created because hibernate-redis has some problems with removing cache entries when configured with a cluster (CROSSLOT issues). So this fork uses redisson PRO and uses the built in "cache-like" features of redis. Configured with "maxmemory 1GB" and "maxmemory-policy allkeys-lru" in the redis.conf. The caveat of this is that it's impossible to configure TTL for cache regions, the last used entry will be flushed when redis memory gets full. As redisson PRO costs, this fork releases against an internal maven server (nexus). To release use: mvn -Darguments=-Dredisson.pro.key="key_from_redisson_pro_email" release:prepare release:perform.

[![Maven Central](https://img.shields.io/maven-central/v/com.github.debop/hibernate-redis.svg)](https://repo1.maven.org/maven2/com/github/debop/hibernate-redis) [![Build Status](https://travis-ci.org/debop/hibernate-redis.png)](https://travis-ci.org/debop/hibernate-redis)

[hibernate][1] (4.x, 5.1.x, 5.2.x) 2nd level cache provider using redis server 3.x. with [Redisson][2] 2.3.x

Reduce cache size by [Redisson][2] SnappyCodec (see [snappy-java][snappy], [Fast-Serialization][fst])

### Note

From 2.2.1 onwards Hibernate region naming (hibernate.cache.region_prefix) has been simplified to "hibernate".

hibernate-core 5.2.x based on Java 8, use hibernate-redis 2.2.0 or higher

Region factory for hibernate 5.2.x is hibernate.redis.cache.hibernate52.SingletonRedisRegionFactory

### Setup

##### Maven Repository

add dependency

```xml
<dependency>
    <groupId>com.github.debop</groupId>
    <artifactId>hibernate-redis</artifactId>
    <version>2.3.2</version>
</dependency>
```

Optional dependencies.
Redisson support various codec (serializer, compression). you can choose other codec. see Redisson Help.

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>${redisson.version}</version>
</dependency>
<dependency>
    <groupId>de.ruedigermoeller</groupId>
    <artifactId>fst</artifactId>
    <version>${fst.version}</version>
</dependency>
<dependency>
    <groupId>org.xerial.snappy</groupId>
    <artifactId>snappy-java</artifactId>
    <version>${snappy-java.version}</version>
</dependency>
```

##### Setup hibernate configuration

Setup hibernate configuration (Note package name for hibernate 4 / hibernate 5 / hibernate52)

```java
// Secondary Cache
props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
props.put(Environment.USE_QUERY_CACHE, true);
props.put(Environment.CACHE_REGION_FACTORY, org.hibernate.cache.redis.hibernate52.SingletonRedisRegionFactory.class.getName());
props.put(Environment.CACHE_REGION_PREFIX, "hibernate");

// Optional setting for second level cache statistics
props.setProperty(Environment.GENERATE_STATISTICS, "true");
props.setProperty(Environment.USE_STRUCTURED_CACHE, "true");

// Hibernate 4
props.setProperty(Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName());

// Configuration for Redis that used by hibernate
props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");
```

also same configuration for using Spring Framework or [Spring Data JPA][4]

### Redis settings for hibernate-redis

sample for hibernate-redis.properties

```ini
 ##########################################################
 #
 # properities for hibernate-redis
 #
 ##########################################################

 # Redisson configuration file
 redisson-config=conf/redisson.yaml
```

sample for Redisson configuration (see [more samples](https://github.com/mrniko/redisson/wiki/2.-Configuration) )

```yaml
# redisson configuration for redis servers
# see : https://github.com/mrniko/redisson/wiki/2.-Configuration

clusterServersConfig:
  idleConnectionTimeout: 10000
  pingTimeout: 1000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500
  reconnectionTimeout: 3000
  failedAttempts: 3
  password: null
  subscriptionsPerConnection: 5
  clientName: null
  loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> {}
  slaveSubscriptionConnectionMinimumIdleSize: 1
  slaveSubscriptionConnectionPoolSize: 50
  slaveConnectionMinimumIdleSize: 5
  slaveConnectionPoolSize: 250
  masterConnectionMinimumIdleSize: 5
  masterConnectionPoolSize: 250
  readMode: "SLAVE"
  nodeAddresses:
  - "//127.0.0.1:6379"
  - "//127.0.0.1:6381"
  - "//127.0.0.1:6382"
  scanInterval: 1000
threads: 0
nettyThreads: 0
codec: !<org.redisson.codec.SnappyCodec> {}
useLinuxNativeEpoll: false
```

### Hibernate configuration via Spring [Application property files](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files)

In Spring applications, the hibernate- and hibernate-redis configuration represented above can be configured within
Spring application property files like below.

```ini
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.use_query_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.redis.hibernate52.SingletonRedisRegionFactory
spring.jpa.properties.hibernate.cache.region_prefix=hibernate

spring.jpa.properties.hibernate.cache.use_structured_entries=true
spring.jpa.properties.hibernate.generate_statistics=true

spring.jpa.properties.redisson-config=classpath:conf/redisson.yaml
```

### Setup hibernate entity to use cache

add @org.hibernate.annotations.Cache annotation to Entity class like this

```java
@Entity
@Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)  // or @Cacheable(true) for JPA
@Getter
@Setter
public class Item implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private static final long serialVersionUID = -281066218676472922L;
}
```

### How to monitor hibernate-cache is running

run "redis-cli monitor" command in terminal. you can see putting cached items, retrieving cached items.

### Sample code

see hibernate-examples module



[1]: http://www.hibernate.org/
[2]: https://github.com/mrniko/redisson
[3]: https://github.com/debop/hibernate-redis/blob/master/hibernate-redis/src/test/java/org/hibernate/test/cache/HibernateCacheTest.java
[4]: http://projects.spring.io/spring-data-jpa/
[lombok]: http://www.projectlombok.org/
[fst]: https://github.com/RuedigerMoeller/fast-serialization
[snappy]: https://github.com/xerial/snappy-java
[benchmark]: https://github.com/debop/hibernate-redis/blob/master/hibernate-redis/src/test/java/org/hibernate/test/serializer/SerializerTest.java
