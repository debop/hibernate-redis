package org.hibernate.test;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cache.redis.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.test.domain.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * org.hibernate.test.HibernateConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 8. 28. 오후 9:33
 */
@Slf4j
@Configuration
public class HibernateConfiguration {

	public String getDatabaseName() {
		return "hibernate";
	}

	public String[] getMappedPackageNames() {
		return new String[] {
				Account.class.getPackage().getName()
		};
	}

	public Properties hibernateProperties() {
		Properties props = new Properties();

		props.put(Environment.FORMAT_SQL, "true");
		props.put(Environment.HBM2DDL_AUTO, "create");
		props.put(Environment.SHOW_SQL, "true");

		// Secondary Cache
		props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
		props.put(Environment.USE_QUERY_CACHE, true);
		props.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
		props.put(Environment.CACHE_REGION_PREFIX, "");
		props.put(Environment.CACHE_PROVIDER_CONFIG, "classpath:redis.properties");

		return props;
	}

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.build();
	}

	@Bean
	public SessionFactory sessionFactory() {

		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		factoryBean.setPackagesToScan(getMappedPackageNames());
		factoryBean.setDataSource(dataSource());
		factoryBean.setHibernateProperties(hibernateProperties());

		try {
			factoryBean.afterPropertiesSet();
		} catch (IOException e) {
			throw new RuntimeException("Fail to build SessionFactory!!!", e);
		}

		return factoryBean.getObject();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new HibernateTransactionManager(sessionFactory());
	}
}
