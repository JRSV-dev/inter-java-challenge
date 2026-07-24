package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;
import com.inter.java.challenge.configuration.exception.CodigoErro;

import static com.inter.java.challenge.utils.MensagensExceptions.USUARIO_COM_IDENTIFICADOR_CADASTRADO;
import static org.springframework.http.HttpStatus.CONFLICT;

public class UsuarioJaExisteIdentificador extends ApiException {

    public UsuarioJaExisteIdentificador() {
        super(
                CodigoErro.USUARIO_IDENTIFICADOR_JA_CADASTRADO,
                CONFLICT,
                USUARIO_COM_IDENTIFICADOR_CADASTRADO
        );
    }
}
