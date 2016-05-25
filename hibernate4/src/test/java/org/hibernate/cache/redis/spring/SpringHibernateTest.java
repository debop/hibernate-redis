package org.hibernate.cache.redis.spring;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cache.redis.hibernate4.util.HibernateCacheUtil;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HibernateRedisConfiguration.class})
public class SpringHibernateTest {

  @Autowired
  SessionFactory sessionFactory;

  @Before
  public void setup() {
    assertThat(sessionFactory).isNotNull();

    sessionFactory.getStatistics().setStatisticsEnabled(true);
    sessionFactory.getStatistics().clear();
  }

  public SecondLevelCacheStatistics getSecondLevelCacheStatistics(Class clazz) {
    String regionName = HibernateCacheUtil.getRegionName(sessionFactory, clazz);
    log.debug("class={}, region={}", clazz.getSimpleName(), regionName);

    return sessionFactory.getStatistics().getSecondLevelCacheStatistics(regionName);
  }

  @Test
  public void emptySecondLevelCacheEntry() throws Exception {

  }

  @Test
  public void queryCacheInvalidation() throws Exception {

  }

  @Test
  public void simpleEntityCaching() throws Exception {

  }

  @Test
  public void hqlLoad() throws Exception {

  }

  @Test
  public void nonrestrictCaching() throws Exception {

  }

  @Test
  public void massiveCaching() throws Exception {

  }
}
