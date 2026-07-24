package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;

import static com.inter.java.challenge.utils.MensagensExceptions.LIMITE_TRANSFERENCIA_EXCEDIDO;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class LimiteDiarioExcedidoException extends ApiException {

    public LimiteDiarioExcedidoException() {
        super(BAD_REQUEST.toString(), BAD_REQUEST.value(), LIMITE_TRANSFERENCIA_EXCEDIDO);
    }
}