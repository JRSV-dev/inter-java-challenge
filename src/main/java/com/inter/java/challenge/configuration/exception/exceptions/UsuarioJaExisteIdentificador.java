package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;

import static com.inter.java.challenge.utils.MensagensExceptions.USUARIO_COM_IDENTIFICADOR_CADASTRADO;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UsuarioJaExisteIdentificador extends ApiException {

    public UsuarioJaExisteIdentificador() {
        super(BAD_REQUEST.toString(), BAD_REQUEST.value(), USUARIO_COM_IDENTIFICADOR_CADASTRADO);
    }
}