hibernate-redis  [![Build Status](https://travis-ci.org/debop/hibernate-redis.png)](https://travis-ci.org/debop/hibernate-redis)
===============

[hibernate][1] (4.2.x.Final, 4.3.x.Final) 2nd level cache using redis server.
with [jedis][2]  2.4.1 or higher

reduce cache size by [Fast-Serialization][fst] and [snappy-java][snappy]. thanks!
try serialization [benchmark][benchmark].

### NOTE

***Don't use Hibernate 4.3.2.Final, 4.2.9.Final!!! It has bug in CacheKey!***
Recommend use 4.3.4.Final or 4.2.10.Final

Hibernate 4.3.2.Final CacheKey eliminate entityOrRoleName property for reduce CacheKey size.
if multiple entity cached in same region, can't figure out wanted entity.

### Maven Repository

add dependency

```xml
<dependency>
    <groupId>com.github.debop</groupId>
    <artifactId>hibernate-redis</artifactId>
    <version>1.6.1</version>
</dependency>
```

add repository

```xml
<repositories>
    <repository>
        <id>debop-releases-bintray</id>
        <url>http://dl.bintray.com/debop/maven</url>
    </repository>
</repositories>
```

### setup hibernate configuration

setup hibernate configuration.

```java
// Secondary Cache
props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
props.put(Environment.USE_QUERY_CACHE, true);
props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
props.put(Environment.CACHE_REGION_PREFIX, "hibernate");

// optional setting for second level cache statistics
props.setProperty(Environment.GENERATE_STATISTICS, "true");
props.setProperty(Environment.USE_STRUCTURED_CACHE, "true");

props.setProperty(Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName());

// configuration for Redis that used by hibernate
props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");
```

also same configuration for using Spring Framework or [Spring Data JPA][4]

### redis settings for hibernate-redis

sample for hibernate-redis.properties

```ini
 ##########################################################
 #
 # properities for hibernate-redis
 #
 ##########################################################

 # Redis Server for hibernate 2nd cache
 redis.host=localhost
 redis.port=6379

 # redis.timeout=2000
 # redis.password=

 # database for hibernate cache
 # redis.database=0
 redis.database=1

 # hiberante 2nd cache default expiry (seconds)
 redis.expiryInSeconds=120

 # expiry of hibernate.common region (seconds) // hibernate is prefix, region name is common
 redis.expiryInSeconds.hibernate.common=0

 # expiry of hibernate.account region (seconds) // hibernate is prefix, region name is account
 redis.expiryInSeconds.hibernate.account=1200
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
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    private static final long serialVersionUID = -281066218676472922L;
}
```

### How to monitor hibernate-cache is running

run "redis-cli monitor" command in terminal. you can see putting cached items, retrieving cached items.

### Sample code

read [HibernateCacheTest.java][3] for more usage.



[1]: http://www.hibernate.org/
[2]: https://github.com/xetorthio/jedis
[3]: https://github.com/debop/hibernate-redis/blob/master/hibernate-redis/src/test/java/org/hibernate/test/cache/HibernateCacheTest.java
[4]: http://projects.spring.io/spring-data-jpa/
[lombok]: http://www.projectlombok.org/
[fst]: https://github.com/RuedigerMoeller/fast-serialization
[snappy]: https://github.com/xerial/snappy-java
[benchmark]: https://github.com/debop/hibernate-redis/blob/master/hibernate-redis/src/test/java/org/hibernate/test/serializer/SerializerTest.java