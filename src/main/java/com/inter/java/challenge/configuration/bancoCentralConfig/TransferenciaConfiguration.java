package com.inter.java.challenge.configuration.bancoCentralConfig;

import com.inter.java.challenge.data.records.BancoCentralProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(BancoCentralProperties.class)
public class TransferenciaConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
