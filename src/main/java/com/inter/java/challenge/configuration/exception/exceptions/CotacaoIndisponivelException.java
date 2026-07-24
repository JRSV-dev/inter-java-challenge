package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;

import static com.inter.java.challenge.utils.MensagensExceptions.COTACAO_INDISPONIVEL;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

public class CotacaoIndisponivelException extends ApiException {

    public CotacaoIndisponivelException() {
        super(
                SERVICE_UNAVAILABLE.toString(),
                SERVICE_UNAVAILABLE.value(),
                COTACAO_INDISPONIVEL
        );
    }
}
