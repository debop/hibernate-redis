package org.hibernate.stresser.persistence.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
@Configuration
@PropertySource("classpath:db.properties")
public class DatabaseConfig {

    @Value("${db.url}")
    private String url;

    @Value("${db.driver}")
    private String driver;

    @Value("${db.user}")
    private String user;

    @Value("${db.password}")
    private String password;

    /**
     * "For maximum performance and responsiveness to spike demands, we recommend not setting this value and instead
     * allowing HikariCP to act as a fixed size connection pool."
     *
     * @see <a href="https://github.com/brettwooldridge/HikariCP/wiki/Configuration">https://github.com/brettwooldridge/HikariCP/wiki/Configuration</a>
     */
    @Value("${db.minimumIdle:0}")
    private int minimumIdle;

    @Value("${db.maximumPoolSize:20}")
    private int maximumPoolSize;

    @Value("${db.idleTimeout:45}")
    private int idleTimeout;

    @Value("${db.connectionTimeout:12}")
    private int connectionTimeout;

    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getMinimumIdle() {
        return minimumIdle;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
