package org.hibernate.examples.utils;

/**
 * DataConst
 *
 * @author Sunghyouk Bae
 */
public final class DataConst {

  private DataConst() {
  }

  public static String DATASOURCE_CLASS_H2 = "org.h2.jdbcx.JdbcDataSource";
  public static String DRIVER_CLASS_H2 = "org.h2.Driver";
  public static String DIALECT_H2 = "org.hibernate.dialect.H2Dialect";

  public static String DATASOURCE_CLASS_HSQL = "org.hsqldb.jdbc.JDBCDataSource";
  public static String DRIVER_CLASS_HSQL = "org.hsqldb.jdbcDriver";
  public static String DIALECT_HSQL = "org.hibernate.dialect.HSQLDialect";

  public static String DATASOURCE_CLASS_MYSQL = "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
  public static String DRIVER_CLASS_MYSQL = "com.mysql.jdbc.Driver";
  public static String DIALECT_MYSQL = "org.hibernate.dialect.MySQL5InnoDBDialect";

  public static String DRIVER_CLASS_MARIADB = "org.mariadb.jdbc.Driver";

  public static String DATASOURCE_CLASS_POSTGRESQL = "org.postgresql.ds.PGSimpleDataSource";
  public static String DRIVER_CLASS_POSTGRESQL = "org.postgresql.Driver";
  public static String DIALECT_POSTGRESQL = "org.hibernate.dialect.PostgreSQL82Dialect";
}
