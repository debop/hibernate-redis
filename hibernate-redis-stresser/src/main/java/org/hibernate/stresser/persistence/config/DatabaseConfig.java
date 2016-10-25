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

    @Value("${db.game.url}")
    private String gameUrl;

    @Value("${db.game.driver}")
    private String gameDriver;

    @Value("${db.game.user}")
    private String gameUser;

    @Value("${db.game.password}")
    private String gamePassword;

    /**
     * "For maximum performance and responsiveness to spike demands, we recommend not setting this value and instead
     * allowing HikariCP to act as a fixed size connection pool."
     *
     * @see <a href="https://github.com/brettwooldridge/HikariCP/wiki/Configuration">https://github.com/brettwooldridge/HikariCP/wiki/Configuration</a>
     */
    @Value("${db.game.minimumIdle:0}")
    private int minimumIdle;

    @Value("${db.game.maximumPoolSize:20}")
    private int maximumPoolSize;

    @Value("${db.game.idleTimeout:45}")
    private int idleTimeout;

    @Value("${db.game.connectionTimeout:12}")
    private int connectionTimeout;

    public String getGameUrl() {
        return gameUrl;
    }

    public String getGameDriver() {
        return gameDriver;
    }

    public String getGameUser() {
        return gameUser;
    }

    public String getGamePassword() {
        return gamePassword;
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
