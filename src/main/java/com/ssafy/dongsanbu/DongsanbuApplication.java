package com.ssafy.dongsanbu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DongsanbuApplication {

    public static void main(String[] args) {
        SpringApplication.run(DongsanbuApplication.class, args);
    }

}
