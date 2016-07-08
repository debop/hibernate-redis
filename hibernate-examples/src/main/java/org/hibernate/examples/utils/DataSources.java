/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.hibernate.examples.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * {@link DataSource} 를 생성, 제공하는 Helper Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 11:04
 */
@Slf4j
public class DataSources {

  private DataSources() {}

  public static final String HSQL_DRIVER_CLASS_NAME = "org.hsql.jdbcDriver";
  public static final String H2_DRIVER_CLASS_NAME = "org.h2.Driver";

  /**
   * {@link DataSource} 를 빌드합니다. 기본적으로 Tomcat DataSource 를 사용합니다.
   *
   * @param driverClass DriverClass 명
   * @param url         Database 주소
   * @return [[javax.sql.DataSource]] 인스턴스
   */
  public static DataSource getDataSource(String driverClass, String url) {
    return getDataSource(driverClass, url, "", "");
  }

  /**
   * {@link DataSource} 를 빌드합니다. 기본적으로 Tomcat DataSource 를 사용합니다.
   *
   * @param driverClass DriverClass 명
   * @param url         Database 주소
   * @param username    사용자 명
   * @param passwd      사용자 패스워드
   * @return [[javax.sql.DataSource]] 인스턴스
   */
  public static DataSource getDataSource(String driverClass, String url, String username, String passwd) {
    return getHikariDataSource(driverClass, url, username, passwd, null);
    // return getBoneCPDataSource(driverClass, url, username, passwd);
    // return getTomcatDataSource(driverClass, url, username, passwd);
  }

  /**
   * HikariCP DataSource를 생성합니다.
   *
   * @param driverClass DriverClass 명
   * @param url         Database 주소
   * @param username    사용자 명
   * @param passwd      사용자 패스워드
   * @return [[javax.sql.DataSource]] 인스턴스
   */
  public static DataSource getHikariDataSource(String driverClass,
                                               String url,
                                               String username,
                                               String passwd,
                                               Properties props) {
    HikariConfig config = new HikariConfig();

    config.setDriverClassName(driverClass);
    config.setJdbcUrl(url);
    config.setUsername(username);
    config.setPassword(passwd);

    // MySQL 인 경우 성능을 위해 아래 설정을 사용합니다.
    if (DataConst.DRIVER_CLASS_MYSQL.equals(driverClass)) {
      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      config.addDataSourceProperty("useServerPrepStmts", "true");
    }

    if (props != null) {
      for (Map.Entry<Object, Object> entry : props.entrySet())
        config.addDataSourceProperty(entry.getKey().toString(), entry.getValue().toString());
    }

    config.setMinimumIdle(2);
    config.setMaximumPoolSize(4 * Runtime.getRuntime().availableProcessors());

    config.setInitializationFailFast(true);
    config.setConnectionTestQuery("SELECT 1");

    return new HikariDataSource(config);
  }

  /**
   * 테스트에 사용하기 위해 메모리를 사용하는 HSql DB 에 대한 DataSource 를 반환합니다.
   */
  public static DataSource getEmbeddedHSqlDataSource() {
    return getDataSource(HSQL_DRIVER_CLASS_NAME, "jdbc:hsqldb:mem:test;MVCC=TRUE;", "", "");
  }


  /**
   * 테스트에 사용하기 위해 메모리를 사용하는 H2 DB 에 대한 DataSource 를 반환합니다.
   */
  public static DataSource getEmbeddedH2DataSource() {
    return getDataSource(H2_DRIVER_CLASS_NAME, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "", "");
  }

}
