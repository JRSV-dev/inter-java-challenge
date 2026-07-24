package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;

import static com.inter.java.challenge.utils.MensagensExceptions.SALDO_INSUFICIENTE;
import static com.inter.java.challenge.utils.MensagensExceptions.USUARIO_COM_EMAIL_CADASTRADO;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class SaldoInsuficienteException extends ApiException {

    public SaldoInsuficienteException() {
        super(BAD_REQUEST.toString(), BAD_REQUEST.value(), SALDO_INSUFICIENTE);
    }
}