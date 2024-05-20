package com.ssafy.ssapay.global.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
    private final WriteDataSourceProperties writeDataSourceProperties;
    private final ReadDataSourceProperties readDataSourceProperties;

    @Bean(name = "writeDataSource")
    public HikariDataSource writeDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(writeDataSourceProperties.getDriver());
        hikariDataSource.setJdbcUrl(writeDataSourceProperties.getUrl());
        hikariDataSource.setUsername(writeDataSourceProperties.getUsername());
        hikariDataSource.setPassword(writeDataSourceProperties.getPassword());
        return hikariDataSource;
    }

    @Bean(name = "readDataSource")
    public HikariDataSource readDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(readDataSourceProperties.getDriver());
        hikariDataSource.setJdbcUrl(readDataSourceProperties.getUrl());
        hikariDataSource.setUsername(readDataSourceProperties.getUsername());
        hikariDataSource.setPassword(readDataSourceProperties.getPassword());
        return hikariDataSource;
    }

    @Bean
    public HibernateProperties hibernateProperties() {
        return new HibernateProperties();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }
}
