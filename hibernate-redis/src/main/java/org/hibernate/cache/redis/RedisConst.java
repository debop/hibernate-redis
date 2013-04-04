package org.hibernate.cache.redis;

/**
 * org.hibernate.cache.redis.RedisConst
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 4. 오후 1:42
 */
public class RedisConst {

    private RedisConst() {}

    private static final String PREFIX = "hibernate.cache.redis.";

    private static final String CONFIG_SUFFIX = ".cfg";

    private static final String STRATEGY_SUFFIX = ".eviction.strategy";

    private static final String WAKE_UP_INTERVAL_SUFFIX = ".eviction.wake_up_interval";

    private static final String MAX_ENTRIES_SUFFIX = ".eviction.max_entries";

    private static final String LIFESPAN_SUFFIX = ".expiration.lifespan";

    private static final String MAX_IDLE_SUFFIX = ".expiration.max_idle";

//   private static final String STATISTICS_SUFFIX = ".statistics";

    /**
     * Classpath or filesystem resource containing Infinispan configurations the factory should use.
     *
     * @see #DEF_INFINISPAN_CONFIG_RESOURCE
     */
    public static final String INFINISPAN_CONFIG_RESOURCE_PROP = "hibernate.cache.redis.cfg";

    public static final String INFINISPAN_GLOBAL_STATISTICS_PROP = "hibernate.cache.redis.statistics";

    /**
     * Property that controls whether Infinispan should interact with the
     * transaction manager as a {@link javax.transaction.Synchronization} or as
     * an XA resource. If the property is set to true, it will be a
     * synchronization, otherwise an XA resource.
     *
     * @see #DEF_USE_SYNCHRONIZATION
     */
    public static final String INFINISPAN_USE_SYNCHRONIZATION_PROP = "hibernate.cache.redis.use_synchronization";

    private static final String NATURAL_ID_KEY = "naturalid";

    /**
     * Name of the configuration that should be used for natural id caches.
     *
     * @see #DEF_ENTITY_RESOURCE
     */
    public static final String NATURAL_ID_CACHE_RESOURCE_PROP = PREFIX + NATURAL_ID_KEY + CONFIG_SUFFIX;

    private static final String ENTITY_KEY = "entity";

    /**
     * Name of the configuration that should be used for entity caches.
     *
     * @see #DEF_ENTITY_RESOURCE
     */
    public static final String ENTITY_CACHE_RESOURCE_PROP = PREFIX + ENTITY_KEY + CONFIG_SUFFIX;

    private static final String COLLECTION_KEY = "collection";

    /**
     * Name of the configuration that should be used for collection caches.
     * No default value, as by default we try to use the same Infinispan cache
     * instance we use for entity caching.
     *
     * @see #ENTITY_CACHE_RESOURCE_PROP
     * @see #DEF_ENTITY_RESOURCE
     */
    public static final String COLLECTION_CACHE_RESOURCE_PROP = PREFIX + COLLECTION_KEY + CONFIG_SUFFIX;

    private static final String TIMESTAMPS_KEY = "timestamps";

    /**
     * Name of the configuration that should be used for timestamp caches.
     *
     * @see #DEF_TIMESTAMPS_RESOURCE
     */
    public static final String TIMESTAMPS_CACHE_RESOURCE_PROP = PREFIX + TIMESTAMPS_KEY + CONFIG_SUFFIX;

    private static final String QUERY_KEY = "query";

    /**
     * Name of the configuration that should be used for query caches.
     *
     * @see #DEF_QUERY_RESOURCE
     */
    public static final String QUERY_CACHE_RESOURCE_PROP = PREFIX + QUERY_KEY + CONFIG_SUFFIX;

    /**
     * Default value for {@link #INFINISPAN_CONFIG_RESOURCE_PROP}. Specifies the "infinispan-configs.xml" file in this package.
     */
    public static final String DEF_INFINISPAN_CONFIG_RESOURCE = "org/hibernate/cache/infinispan/builder/infinispan-configs.xml";

    /**
     * Default value for {@link #ENTITY_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_ENTITY_RESOURCE = "entity";

    /**
     * Default value for {@link #TIMESTAMPS_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_TIMESTAMPS_RESOURCE = "timestamps";

    /**
     * Default value for {@link #QUERY_CACHE_RESOURCE_PROP}.
     */
    public static final String DEF_QUERY_RESOURCE = "local-query";

    /**
     * Default value for {@link #INFINISPAN_USE_SYNCHRONIZATION_PROP}.
     */
    public static final boolean DEF_USE_SYNCHRONIZATION = true;

    /**
     * Name of the pending puts cache.
     */
    public static final String PENDING_PUTS_CACHE_NAME = "pending-puts";
}
