package ru.phyllosedis.platform.banking.impl.config;

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
        basePackages = "ru.phyllosedis.platform.banking.impl.repository",
        entityManagerFactoryRef = "bankingEntityManagerFactory",
        transactionManagerRef = "bankingTransactionManager"
)
public class BankingDbConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.datasource.banking")
    public DataSourceProperties bankingDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "bankingDataSource")
    @ConfigurationProperties(prefix = "app.datasource.banking")
    public DataSource bankingDataSource() {
        return new HikariDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "app.jpa.banking")
    public Map<String, String> bankingJpaProperties() {
        return new HashMap<>();
    }

    @Bean(name = "bankingEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean bankingEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("bankingDataSource") DataSource bankingDataSource,
            @Qualifier("bankingJpaProperties") Map<String, String> bankingJpaProps) {
        Map<String, Object> properties = new HashMap<>(bankingJpaProps);
        return builder
                .dataSource(bankingDataSource)
                .packages("ru.phyllosedis.platform.banking.impl.model.entity")
                .persistenceUnit("banking")
                .properties(properties)
                .build();
    }

    @Bean(name = "bankingTransactionManager")
    public TransactionManager bankingTransactionManager(@Qualifier("bankingEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "bankingLiquibase")
    public SpringLiquibase bankingLiquibase(@Qualifier("bankingDataSource") DataSource bankingDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(bankingDataSource);
        liquibase.setChangeLog("classpath:db/banking/changelog.xml");
        return liquibase;
    }
}
