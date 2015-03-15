package org.hibernate.test.cache.timestamper;

import org.hibernate.cache.redis.jedis.JedisClient;
import org.hibernate.cache.redis.timestamper.JedisCacheTimestamperJedisImpl;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.TestingSettingsBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JedisCacheTimestamperJedisImplTest {
    public static final String CACHE_REGION_PREFIX_FIELD_NAME = "cacheRegionPrefix";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private JedisClient jedisClient;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private TestingSettingsBuilder settingsBuilder;

    private Settings settings;

    private Properties properties;

    private JedisCacheTimestamperJedisImpl timestamper;

    @Before
    public void setUp() throws Exception {
        settingsBuilder = new TestingSettingsBuilder();
        properties = new Properties();
    }

    @Test
    public void constructor() throws Exception {
        givenTimestamperWithCacheRegionPrefix("cachetest");

        String timestampCacheKet = timestamper.getTimestampCacheKey();

        assertThat(timestampCacheKet).isEqualTo("cachetest." + JedisCacheTimestamperJedisImpl.TIMESTAMP_KEY);
    }

    @Test
    public void constructor_without_cacheRegionPrefix() throws Exception {
        givenTimestamperWithCacheRegionPrefix(null);

        String timestampCacheKey = timestamper.getTimestampCacheKey();

        assertThat(timestampCacheKey).isEqualTo(JedisCacheTimestamperJedisImpl.TIMESTAMP_KEY);
    }

    @Test
    public void next() throws Exception {
        givenTimestamperWithCacheRegionPrefix("myservice");

        String cacheKey = timestamper.getTimestampCacheKey();

        long expected = 12345L;
        when(jedisClient.nextTimestamp(eq(cacheKey))).thenReturn(expected);

        long next = timestamper.next();

        assertThat(next).isEqualTo(expected);
    }

    private void givenTimestamperWithCacheRegionPrefix(String cacheRegionPrefix) {
        settings = settingsBuilder.setField(CACHE_REGION_PREFIX_FIELD_NAME, cacheRegionPrefix).build();

        timestamper = new JedisCacheTimestamperJedisImpl();
        timestamper.setSettings(settings);
        timestamper.setProperties(properties);
        timestamper.setJedisClient(jedisClient);
    }
}