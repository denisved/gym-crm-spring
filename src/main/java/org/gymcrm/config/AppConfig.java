package org.gymcrm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "org.gymcrm")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public Map<String, Map<Long, Object>> commonStorageMap() {
        return new HashMap<>();
    }
}