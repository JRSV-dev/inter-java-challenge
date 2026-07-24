package com.inter.java.challenge.configuration.bancoCentralConfig;

import com.inter.java.challenge.data.records.BancoCentralProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(BancoCentralProperties.class)
public class TransferenciaConfiguration {

    @Bean
    public RestClient bancoCentralRestClient(
            RestClient.Builder builder,
            BancoCentralProperties properties
    ) {
        return builder
                .baseUrl(properties.baseUrl())
                .build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}