package ru.phyllosedis.platform.app.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import java.util.HashMap;

/**
 * Общая JPA-инфраструктура для multi-datasource монолита.
 * {@code HibernateJpaAutoConfiguration} откатывается, когда DataSource
 * несколько (нет single-candidate), поэтому vendor-адаптер и билдер
 * объявляем вручную. Доменные конфиги (*DbConfig) используют этот билдер.
 */
@Configuration
public class JpaCommonConfig {

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
            HibernateJpaVendorAdapter jpaVendorAdapter,
            ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
        return new EntityManagerFactoryBuilder(
                jpaVendorAdapter,
                new HashMap<>(),
                persistenceUnitManager.getIfAvailable());
    }
}
