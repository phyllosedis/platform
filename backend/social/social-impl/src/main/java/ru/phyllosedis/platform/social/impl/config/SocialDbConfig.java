package ru.phyllosedis.platform.social.impl.config;

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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "ru.phyllosedis.platform.social.impl.repository",
        entityManagerFactoryRef = "socialEntityManagerFactory",
        transactionManagerRef = "socialTransactionManager"
)
public class SocialDbConfig {

    @Bean(name = "socialDataSourceProperties")
    @ConfigurationProperties("app.datasource.social")
    public DataSourceProperties socialDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "socialDataSource")
    @ConfigurationProperties("app.datasource.social")
    public DataSource socialDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "socialJpaProperties")
    @ConfigurationProperties("app.jpa.social")
    public Map<String, String> socialJpaProperties() {
        return new HashMap<>();
    }

    @Bean(name = "socialEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean socialEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("socialDataSource") DataSource socialDataSource,
            @Qualifier("socialJpaProperties") Map<String, String> socialJpaProps) {
        Map<String, Object> properties = new HashMap<>(socialJpaProps);
        return builder
                .dataSource(socialDataSource)
                .packages("ru.phyllosedis.platform.social.impl.model.entity")
                .persistenceUnit("social")
                .properties(properties)
                .build();
    }

    @Bean(name = "socialTransactionManager")
    public PlatformTransactionManager socialTransactionManager(
            @Qualifier("socialEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "socialLiquibase")
    public SpringLiquibase socialLiquibase(@Qualifier("socialDataSource") DataSource socialDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(socialDataSource);
        liquibase.setChangeLog("classpath:db/social/changelog.xml");
        return liquibase;
    }
}
