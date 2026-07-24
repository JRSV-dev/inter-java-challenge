package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;
import com.inter.java.challenge.configuration.exception.CodigoErro;

import static com.inter.java.challenge.utils.MensagensExceptions.COTACAO_INDISPONIVEL;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class CotacaoIndisponivelException extends ApiException {

    public CotacaoIndisponivelException() {
        this(null);
    }

    public CotacaoIndisponivelException(Throwable cause) {
        super(
                CodigoErro.COTACAO_INDISPONIVEL,
                SERVICE_UNAVAILABLE,
                COTACAO_INDISPONIVEL,
                cause
        );
    }
}
