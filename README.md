hibernate-redis
===============

[hibernate][1] (3.6.x-Final, 4.2.x-Final) 2nd level cache using redis.
use [jedis][2]  2.2.1 or higher


### Usage

##### referencing hibernate-redis

I'm does not know register hibernate-redis to maven. just using source or jar in lib.
and use [lombok][lombok] for getter/setter.

##### setup hibernate configuration

setup hibernate configuration.

    // Secondary Cache
    props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
    props.put(Environment.USE_QUERY_CACHE, true);
    props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
    props.put(Environment.CACHE_REGION_PREFIX, "hibernate:");

    // optional setting for second level cache statistics
    props.setProperty(Environment.GENERATE_STATISTICS, "true");
    props.setProperty(Environment.USE_STRUCTURED_CACHE, "true");

    props.setProperty(Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName());

    // configuration for Redis that used by hibernate
    props.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");

also same configuration for using Spring Framework or [Spring Data JPA][4]

##### Setup hibernate entity to use cache

add @org.hibernate.annotations.Cache annotation to Entity class like this

	@Entity
	@Cache(region = "redis:common", usage = CacheConcurrencyStrategy.READ_WRITE)
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


##### How to monitor hibernate-cache is running

run "redis-cli monitor" command in terminal. you can see putting cached items, retrieving cached items.

##### Sample code

read [HibernateCacheTest.java][3] for more usage.

[1]: http://www.hibernate.org/
[2]: https://github.com/xetorthio/jedis
[3]: https://github.com/debop/hibernate-redis/blob/master/hibernate-redis/src/test/java/org/hibernate/test/cache/HibernateCacheTest.java
[4]: http://projects.spring.io/spring-data-jpa/
[lombok]: http://www.projectlombok.org/