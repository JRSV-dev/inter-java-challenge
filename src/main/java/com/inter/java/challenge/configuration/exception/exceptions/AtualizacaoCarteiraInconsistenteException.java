package com.inter.java.challenge.configuration.exception.exceptions;

import com.inter.java.challenge.configuration.exception.ApiException;
import com.inter.java.challenge.configuration.exception.CodigoErro;

import static com.inter.java.challenge.utils.MensagensExceptions.ATUALIZACAO_CARTEIRA_INCONSISTENTE;
import static org.springframework.http.HttpStatus.CONFLICT;

public class AtualizacaoCarteiraInconsistenteException
        extends ApiException {

    public AtualizacaoCarteiraInconsistenteException() {
        super(
                CodigoErro.ATUALIZACAO_CARTEIRA_INCONSISTENTE,
                CONFLICT,
                ATUALIZACAO_CARTEIRA_INCONSISTENTE
        );
    }
}
