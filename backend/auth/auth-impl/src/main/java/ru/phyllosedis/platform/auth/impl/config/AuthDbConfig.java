package ru.phyllosedis.platform.auth.impl.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.phyllosedis.platform.auth.impl.repository",
        entityManagerFactoryRef = "authEntityManagerFactory",
        transactionManagerRef = "authTransactionManager"
)
public class AuthDbConfig {
    @Bean(name = "authDataSourceProperties")
    @ConfigurationProperties("app.datasource.auth")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "authDataSource")
    @ConfigurationProperties("app.datasource.auth")
    public DataSource authDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "authJpaProperties")
    @ConfigurationProperties("app.jpa.auth")
    public Map<String, String> authJpaProperties() {
        return new HashMap<>();
    }

    @Bean(name = "authEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean authEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("authDataSource") DataSource authDataSource,
            @Qualifier("authJpaProperties") Map<String, String> authJpaProps) {
        Map<String, Object> properties = new HashMap<>(authJpaProps);
        return builder
                .dataSource(authDataSource)
                .packages("ru.phyllosedis.platform.auth.impl.model.entity")
                .persistenceUnit("auth")
                .properties(properties)
                .build();
    }

    @Bean(name = "authTransactionManager")
    public TransactionManager authTransactionManager(@Qualifier("authEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "authLiquibase")
    public SpringLiquibase authLiquibase(@Qualifier("authDataSource") DataSource authDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(authDataSource);
        liquibase.setChangeLog("classpath:db/auth/changelog.xml");
        return liquibase;
    }
}
