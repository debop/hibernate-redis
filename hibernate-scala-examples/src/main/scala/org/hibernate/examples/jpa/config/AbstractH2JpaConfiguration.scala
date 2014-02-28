package org.hibernate.examples.jpa.config

import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource
import java.util.Properties
import org.hibernate.cfg.AvailableSettings
import org.hibernate.examples._

/**
 * org.hibernate.examples.jpa.config.AbstractHSqlJpaConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:07
 */
@Configuration
@EnableTransactionManagement
abstract class AbstractH2JpaConfiguration extends AbstractJpaConfiguration {

    override def dataSource(): DataSource = {
        buildDataSource(DRIVER_CLASS_H2,
                           "jdbc:h2:mem:" + getDatabaseName + ";MVCC=TRUE;", "sa", "")
    }

    override def jpaProperties(): Properties = {
        val props: Properties = super.jpaProperties()
        props.put(AvailableSettings.DIALECT, DIALECT_H2)
        props
    }
}
