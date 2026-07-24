package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;
import com.inter.java.challenge.configuration.exception.CodigoErro;

import static com.inter.java.challenge.utils.MensagensExceptions.USUARIO_COM_EMAIL_CADASTRADO;
import static org.springframework.http.HttpStatus.CONFLICT;

public class UsuarioJaExisteEmail extends ApiException {

    public UsuarioJaExisteEmail() {
        super(
                CodigoErro.USUARIO_EMAIL_JA_CADASTRADO,
                CONFLICT,
                USUARIO_COM_EMAIL_CADASTRADO
        );
    }
}
