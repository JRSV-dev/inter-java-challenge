package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;
import com.inter.java.challenge.configuration.exception.CodigoErro;

import static com.inter.java.challenge.utils.MensagensExceptions.LIMITE_TRANSFERENCIA_EXCEDIDO;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class LimiteDiarioExcedidoException extends ApiException {

    public LimiteDiarioExcedidoException() {
        super(
                CodigoErro.LIMITE_DIARIO_EXCEDIDO,
                UNPROCESSABLE_ENTITY,
                LIMITE_TRANSFERENCIA_EXCEDIDO
        );
    }
}
