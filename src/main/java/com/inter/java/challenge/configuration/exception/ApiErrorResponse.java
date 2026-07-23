package com.inter.java.challenge.configuration.exception;


import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        Integer status,
        String codigo,
        String mensagem,
        String path
) {
}