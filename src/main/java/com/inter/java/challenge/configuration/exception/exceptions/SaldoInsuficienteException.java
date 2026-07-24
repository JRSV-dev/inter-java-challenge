package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;
import com.inter.java.challenge.configuration.exception.CodigoErro;

import static com.inter.java.challenge.utils.MensagensExceptions.SALDO_INSUFICIENTE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

public class SaldoInsuficienteException extends ApiException {

    public SaldoInsuficienteException() {
        super(
                CodigoErro.SALDO_INSUFICIENTE,
                UNPROCESSABLE_ENTITY,
                SALDO_INSUFICIENTE
        );
    }
}
