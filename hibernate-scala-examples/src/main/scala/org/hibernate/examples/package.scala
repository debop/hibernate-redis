package org.hibernate

/**
 * package 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
package object examples {

    val DRIVER_CLASS_H2: String = "org.h2.Driver"
    val DIALECT_H2: String = "org.hibernate.dialect.H2Dialect"

    val DRIVER_CLASS_HSQL: String = "org.hsqldb.jdbcDriver"
    val DIALECT_HSQL: String = "org.hibernate.dialect.HSQLDialect"

    val DRIVER_CLASS_MYSQL: String = "com.mysql.jdbc.Driver"
    val DIALECT_MYSQL: String = "org.hibernate.dialect.MySQL5InnoDBDialect"

    val DRIVER_CLASS_MARIADB: String = "org.mariadb.jdbc.Driver"

    val DRIVER_CLASS_POSTGRESQL: String = "org.postgresql.Driver"
    val DIALECT_POSTGRESQL: String = "org.hibernate.dialect.PostgreSQL82Dialect"
}
