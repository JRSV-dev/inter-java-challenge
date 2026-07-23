package com.inter.java.challenge.workflows.validator.validarUsuarioRequest.validacoes;

public interface Validador<T> {

    void validar(T objeto);
}