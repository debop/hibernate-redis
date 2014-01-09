package org.hibernate.examples.jpa.config

import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement
import scala.Predef.String
import javax.sql.DataSource
import java.util.Properties
import org.hibernate.cfg.AvailableSettings

/**
 * org.hibernate.examples.jpa.config.AbstractHSqlJpaConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:07
 */
@Configuration
@EnableTransactionManagement
abstract class AbstractHSqlJpaConfiguration extends AbstractJpaConfiguration {

    val DRIVER_CLASS_HSQL: String = "org.hsqldb.jdbcDriver"
    val DIALECT_HSQL: String = "org.hibernate.dialect.HSQLDialect"

    override def dataSource(): DataSource = {
        buildDataSource(DRIVER_CLASS_HSQL, "jdbc:hsqldb:mem:" + getDatabaseName + ";MVCC=TRUE;", "sa", "")
    }
    override def jpaProperties(): Properties = {
        val props: Properties = super.jpaProperties()
        props.put(AvailableSettings.DIALECT, DIALECT_HSQL)
        props
    }
}
