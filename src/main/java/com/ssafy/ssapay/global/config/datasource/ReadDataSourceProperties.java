package com.ssafy.ssapay.global.config.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.datasource.slave")
public class ReadDataSourceProperties {
    private String driver;
    private String url;
    private String username;
    private String password;
}