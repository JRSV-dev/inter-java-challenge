package com.inter.java.challenge.configuration.exception.exceptions;


import com.inter.java.challenge.configuration.exception.ApiException;

import static com.inter.java.challenge.utils.MensagensExceptions.USUARIO_NAO_ENCONTRADO;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class UsuarioNaoEncontradoException extends ApiException {

    public UsuarioNaoEncontradoException() {
        super(NOT_FOUND.toString(), NOT_FOUND.value(), USUARIO_NAO_ENCONTRADO);
    }
}