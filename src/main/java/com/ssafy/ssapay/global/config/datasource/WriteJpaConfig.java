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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = "com.ssafy.ssapay.infra.repository.write",
        entityManagerFactoryRef = "writeEntityManagerFactory",
        transactionManagerRef = "writeTransactionManager"
)
public class WriteJpaConfig {
    private final HikariDataSource writeDataSource;
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;
    private final JpaVendorAdapter jpaVendorAdapter;

    public WriteJpaConfig(@Qualifier("writeDataSource") HikariDataSource writeDataSource,
                          JpaProperties jpaProperties,
                          HibernateProperties hibernateProperties,
                          JpaVendorAdapter jpaVendorAdapter) {
        log.info("writeDataSource.getJdbcUrl() : " + writeDataSource.getJdbcUrl());
        this.writeDataSource = writeDataSource;
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
        this.jpaVendorAdapter = jpaVendorAdapter;
    }

    @Bean("writeEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean writeEntityManagerFactory() {
        Map<String, Object> props = hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(),
                new HibernateSettings());

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(writeDataSource);
        factoryBean.setPackagesToScan("com.ssafy.ssapay");
        factoryBean.setPersistenceUnitName("write");
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setJpaPropertyMap(props);

        return factoryBean;
    }

    @Bean("writeTransactionManager")
    @Primary
    public PlatformTransactionManager writeTransactionManager(
            @Qualifier("writeEntityManagerFactory") EntityManagerFactory writeEntityManagerFactory) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(writeEntityManagerFactory);
        return tm;
    }
}