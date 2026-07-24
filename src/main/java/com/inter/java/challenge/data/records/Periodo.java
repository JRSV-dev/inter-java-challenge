package com.inter.java.challenge.data.records;

import java.time.LocalDateTime;

public record Periodo(
        LocalDateTime inicio,
        LocalDateTime fim
) {
}