package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;

import static com.inter.java.challenge.utils.MensagensExceptions.CARTEIRA_NAO_CADASTRADA_USUARIO;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CarteiraNaoEncontrada extends ApiException {

    public CarteiraNaoEncontrada() {
        super(NOT_FOUND.toString(), NOT_FOUND.value(), CARTEIRA_NAO_CADASTRADA_USUARIO);
    }
}