package org.hibernate.examples.hibernate.config

import java.util.Properties
import javax.sql.DataSource
import org.hibernate.cfg.{AvailableSettings, NamingStrategy}
import org.hibernate.{Interceptor, SessionFactory}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.orm.hibernate4.HibernateExceptionTranslator
import org.springframework.orm.hibernate4.{HibernateTransactionManager, LocalSessionFactoryBean}
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.hibernate.examples.utils.{DataSources, Logger}
import org.hibernate.examples.interceptor.PersistentObjectInterceptor

/**
 * Spring 용 Hibernate 환경 설정 Class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:05
 */
@Configuration
@EnableTransactionManagement
abstract class AbstractHibernateConfiguration {

    lazy val log = Logger[AbstractHibernateConfiguration]

    def getDatabaseName = "hibernate"

    def getMappedPackageNames: Array[String]

    def getNamingStrategy: NamingStrategy = null

    def hibernateProperties(): Properties = {
        val props = new Properties()

        props.setProperty(AvailableSettings.FORMAT_SQL, "true")
        props.setProperty(AvailableSettings
                          .HBM2DDL_AUTO, "create") // create | create-drop | spawn | spawn-drop | update | validate | none
        props.setProperty(AvailableSettings.POOL_SIZE, "30")
        props.setProperty(AvailableSettings.SHOW_SQL, "true")
        props.setProperty(AvailableSettings.FORMAT_SQL, "true")
        props.setProperty(AvailableSettings.AUTOCOMMIT, "true")

        props
    }

    def buildDataSource(driverClass: String, url: String, username: String, password: String): DataSource =
        DataSources.getDataSource(driverClass, url, username, password)

    def buildEmbeddedDataSource() = DataSources.getEmbeddedHSqlDataSource


    @Bean
    def dataSource(): DataSource = buildEmbeddedDataSource()

    protected def setupSessionFactory(factoryBean: LocalSessionFactoryBean) {
        // 추가 작업 시 override 해서 사용하세요.
    }

    @Bean
    def sessionFactory(): SessionFactory = {
        log.info("SessionFactory를 생성합니다.")

        val factoryBean = new LocalSessionFactoryBean()
        val packagenames = getMappedPackageNames
        if (packagenames != null && packagenames.length > 0) {
            log.debug("hibernate용 entity를 scan 합니다. packages=[{}]", packagenames)
            factoryBean.setPackagesToScan(packagenames: _*)
        }

        factoryBean.setNamingStrategy(getNamingStrategy)

        factoryBean.setHibernateProperties(hibernateProperties())
        factoryBean.setDataSource(dataSource())
        val interceptor = hibernateInterceptor()
        if (interceptor != null)
            factoryBean.setEntityInterceptor(hibernateInterceptor())

        setupSessionFactory(factoryBean)

        factoryBean.afterPropertiesSet()
        log.info("SessionFactory Bean에 대해 설정합니다.")

        factoryBean.getObject
    }

    @Bean
    def transactionManager(): HibernateTransactionManager =
        new HibernateTransactionManager(sessionFactory())

    @Bean
    def hibernateInterceptor(): Interceptor = new PersistentObjectInterceptor()

    @Bean
    def hibernateExceptionTranslator() = new HibernateExceptionTranslator()

    @Bean
    def exceptionTranslation(): PersistenceExceptionTranslationPostProcessor =
        new PersistenceExceptionTranslationPostProcessor()
}
