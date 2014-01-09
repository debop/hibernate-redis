package org.hibernate.examples.jpa.config

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.hibernate.examples.utils.{DataSources, Logger}
import org.hibernate.cfg.{AvailableSettings, NamingStrategy}
import java.util.Properties
import javax.sql.DataSource
import org.springframework.orm.hibernate4.HibernateExceptionTranslator
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import javax.persistence.EntityManagerFactory
import org.springframework.orm.jpa.{JpaTransactionManager, LocalContainerEntityManagerFactoryBean}
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager

/**
 * org.hibernate.examples.jpa.config.AbstractJpaConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:02
 */
@Configuration
@EnableTransactionManagement
abstract class AbstractJpaConfiguration {


    lazy val log = Logger[AbstractJpaConfiguration]

    def getDatabaseName = "hibernate"

    def getMappedPackageNames: Array[String]

    def getNamingStrategy: NamingStrategy = null

    def jpaProperties(): Properties = {
        val props = new Properties()

        props.setProperty(AvailableSettings.FORMAT_SQL, "true")
        // create | create-drop | spawn | spawn-drop | update | validate | none
        props.setProperty(AvailableSettings.HBM2DDL_AUTO, "create")
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

    protected def setupEntityManagerFactory(factoryBean: LocalContainerEntityManagerFactoryBean) {
        // 추가 작업 시 override 해서 사용하세요.
    }

    @Bean
    def entityManagerFactory(): EntityManagerFactory = {
        log.info("SessionFactory를 생성합니다.")

        val factoryBean = new LocalContainerEntityManagerFactoryBean()

        val packagenames = getMappedPackageNames
        if (packagenames != null && packagenames.length > 0) {
            log.debug("hibernate용 entity를 scan 합니다. packages=[{}]", packagenames)
            factoryBean.setPackagesToScan(packagenames: _*)
        }
        factoryBean.setJpaProperties(jpaProperties())
        factoryBean.setDataSource(dataSource())

        val adapter = new HibernateJpaVendorAdapter()
        adapter.setGenerateDdl(true)
        factoryBean.setJpaVendorAdapter(adapter)

        setupEntityManagerFactory(factoryBean)

        factoryBean.afterPropertiesSet()
        log.info("EntityManagerFactory Bean에 대해 설정합니다.")

        factoryBean.getObject
    }

    @Bean
    def transactionManager(): PlatformTransactionManager =
        new JpaTransactionManager(entityManagerFactory())

    @Bean
    def hibernateExceptionTranslator() = new HibernateExceptionTranslator()

    @Bean
    def exceptionTranslation(): PersistenceExceptionTranslationPostProcessor =
        new PersistenceExceptionTranslationPostProcessor()
}
