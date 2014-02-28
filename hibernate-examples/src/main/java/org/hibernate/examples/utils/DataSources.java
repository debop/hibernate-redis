package org.hibernate.examples.utils;

import com.jolbox.bonecp.BoneCPDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.sql.DataSource;

/**
 * {@link javax.sql.DataSource} 를 생성, 제공하는 Helper Object 입니다.
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
     * {@link javax.sql.DataSource} 를 빌드합니다. 기본적으로 Tomcat DataSource 를 사용합니다.
     *
     * @param driverClass DriverClass 명
     * @param url         Database 주소
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    public static DataSource getDataSource(String driverClass, String url) {
        return getDataSource(driverClass, url, "", "");
    }

    /**
     * {@link javax.sql.DataSource} 를 빌드합니다. 기본적으로 Tomcat DataSource 를 사용합니다.
     *
     * @param driverClass DriverClass 명
     * @param url         Database 주소
     * @param username    사용자 명
     * @param passwd      사용자 패스워드
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    public static DataSource getDataSource(String driverClass, String url, String username, String passwd) {
        return getBoneCPDataSource(driverClass, url, username, passwd);
        // return getTomcatDataSource(driverClass, url, username, passwd);
    }

    /**
     * BoneCP DataSource 를 빌드합니다.
     *
     * @param driverClass DriverClass 명
     * @param url         Database 주소
     * @param username    사용자 명
     * @param passwd      사용자 패스워드
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    public static DataSource getBoneCPDataSource(String driverClass, String url, String username, String passwd) {

        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setDriverClass(driverClass);
        ds.setJdbcUrl(url);
        ds.setUser(username);
        ds.setPassword(passwd);

        int processCount = Runtime.getRuntime().availableProcessors();

        ds.setMaxConnectionsPerPartition(100);
        ds.setMinConnectionsPerPartition(processCount);
        ds.setPartitionCount(4);

        ds.setIdleMaxAgeInSeconds(120);
        ds.setIdleConnectionTestPeriodInSeconds(60);
        ds.setMaxConnectionAgeInSeconds(300);

        ds.setDisableJMX(true);

        return ds;
    }

    /**
     * Tomcat DataSource 를 빌드합니다.
     *
     * @param driverClass DriverClass 명
     * @param url         Database 주소
     * @param username    사용자 명
     * @param passwd      사용자 패스워드
     * @return [[javax.sql.DataSource]] 인스턴스
     */
    public static DataSource getTomcatDataSource(String driverClass, String url, String username, String passwd) {

        log.debug("Tomcat DataSource를 빌드합니다... driverClass=[{}], url=[{}], username=[{}], passwd=[{}]",
                  driverClass, url, username, passwd);

        PoolProperties p = new org.apache.tomcat.jdbc.pool.PoolProperties();
        p.setUrl(url);
        p.setDriverClassName(driverClass);
        p.setUsername(username);
        p.setPassword(passwd);

        p.setJmxEnabled(true);
        p.setTestWhileIdle(true);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(200);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);

        return new org.apache.tomcat.jdbc.pool.DataSource(p);
    }


    /**
     * 테스트에 사용하기 위해 메모리를 사용하는 HSql DB 에 대한 DataSource 를 반환합니다.
     */
    public static DataSource getEmbeddedHSqlDataSource() {
        return getDataSource(HSQL_DRIVER_CLASS_NAME, "jdbc:hsqldb:mem:test;MVCC=TRUE;", "sa", "");
    }


    /**
     * 테스트에 사용하기 위해 메모리를 사용하는 H2 DB 에 대한 DataSource 를 반환합니다.
     */
    public static DataSource getEmbeddedH2DataSource() {
        return getDataSource(H2_DRIVER_CLASS_NAME, "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MVCC=TRUE;", "sa", "");
    }

}
