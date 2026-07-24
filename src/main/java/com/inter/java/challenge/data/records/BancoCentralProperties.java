package com.inter.java.challenge.data.records;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integracoes.banco-central")
public record BancoCentralProperties(
        String baseUrl,
        int diasRetroativos
) {
}