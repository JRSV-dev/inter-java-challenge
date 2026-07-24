package com.inter.java.challenge.configuration.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public final class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Erro de negócio. code={}, method={}, path={}, message={}",
                exception.getCodigo(),
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

        var response = new ApiErrorResponse(
                LocalDateTime.now(),
                exception.getStatus(),
                exception.getCodigo(),
                exception.getMensagensExceptions().getMensagem(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(response);
    }
}