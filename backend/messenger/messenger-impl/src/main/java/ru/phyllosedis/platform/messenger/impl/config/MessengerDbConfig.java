package ru.phyllosedis.platform.messenger.impl.config;

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
        basePackages = "ru.phyllosedis.platform.messenger.impl.repository",
        entityManagerFactoryRef = "messengerEntityManagerFactory",
        transactionManagerRef = "messengerTransactionManager"
)
public class MessengerDbConfig {

    @Bean(name = "messengerDataSourceProperties")
    @ConfigurationProperties("app.datasource.messenger")
    public DataSourceProperties messengerDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "messengerDataSource")
    @ConfigurationProperties("app.datasource.messenger")
    public DataSource messengerDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "messengerJpaProperties")
    @ConfigurationProperties("app.jpa.messenger")
    public Map<String, String> messengerJpaProperties() {
        return new HashMap<>();
    }

    @Bean(name = "messengerEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean messengerEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("messengerDataSource") DataSource messengerDataSource,
            @Qualifier("messengerJpaProperties") Map<String, String> messengerJpaProps) {
        Map<String, Object> properties = new HashMap<>(messengerJpaProps);
        return builder
                .dataSource(messengerDataSource)
                .packages("ru.phyllosedis.platform.messenger.impl.model.entity")
                .persistenceUnit("messenger")
                .properties(properties)
                .build();
    }

    @Bean(name = "messengerTransactionManager")
    public TransactionManager messengerTransactionManager(
            @Qualifier("messengerEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "messengerLiquibase")
    public SpringLiquibase messengerLiquibase(@Qualifier("messengerDataSource") DataSource messengerDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(messengerDataSource);
        liquibase.setChangeLog("classpath:db/messenger/changelog.xml");
        return liquibase;
    }
}
