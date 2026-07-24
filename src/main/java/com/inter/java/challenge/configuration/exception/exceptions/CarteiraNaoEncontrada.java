package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;
import com.inter.java.challenge.configuration.exception.CodigoErro;

import static com.inter.java.challenge.utils.MensagensExceptions.CARTEIRA_NAO_CADASTRADA_USUARIO;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CarteiraNaoEncontrada extends ApiException {

    public CarteiraNaoEncontrada() {
        super(
                CodigoErro.CARTEIRA_NAO_ENCONTRADA,
                NOT_FOUND,
                CARTEIRA_NAO_CADASTRADA_USUARIO
        );
    }
}
