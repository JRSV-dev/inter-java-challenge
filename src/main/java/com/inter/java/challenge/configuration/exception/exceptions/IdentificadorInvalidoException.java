package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;
import com.inter.java.challenge.configuration.exception.CodigoErro;

import static com.inter.java.challenge.utils.MensagensExceptions.IDENTIFICADOR_INVALIDO;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class IdentificadorInvalidoException extends ApiException {

    public IdentificadorInvalidoException() {
        super(
                CodigoErro.IDENTIFICADOR_INVALIDO,
                BAD_REQUEST,
                IDENTIFICADOR_INVALIDO
        );
    }
}
