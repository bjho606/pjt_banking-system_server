package com.ssafy.ssapay.global.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = "com.ssafy.ssapay.infra.repository.read",
        entityManagerFactoryRef = "readEntityManagerFactory",
        transactionManagerRef = "readTransactionManager"
)
public class ReadJpaConfig {
    private HikariDataSource readDataSource;
    private JpaProperties jpaProperties;
    private HibernateProperties hibernateProperties;
    private JpaVendorAdapter jpaVendorAdapter;

    public ReadJpaConfig(@Qualifier("readDataSource") HikariDataSource readDataSource,
                         JpaProperties jpaProperties,
                         HibernateProperties hibernateProperties,
                         JpaVendorAdapter jpaVendorAdapter) {
        log.info("readDataSource.getJdbcUrl() : " + readDataSource.getJdbcUrl());
        this.readDataSource = readDataSource;
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
        this.jpaVendorAdapter = jpaVendorAdapter;
    }

    @Bean("readEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean readEntityManagerFactory() {
        Map<String, Object> props = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(),
                new HibernateSettings());

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(readDataSource);
        factoryBean.setPackagesToScan("com.ssafy.ssapay");
        factoryBean.setPersistenceUnitName("read");
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setJpaPropertyMap(props);

        return factoryBean;
    }

    @Bean("readTransactionManager")
    public PlatformTransactionManager readTransactionManager(
            @Qualifier("readEntityManagerFactory") EntityManagerFactory readEntityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(readEntityManagerFactory);
        return tm;
    }
}