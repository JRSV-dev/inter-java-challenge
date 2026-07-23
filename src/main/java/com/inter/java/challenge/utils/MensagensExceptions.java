package com.inter.java.challenge.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MensagensExceptions {

    USUARIO_NAO_ENCONTRADO("Usuário não encontrado."),
    USUARIO_COM_EMAIL_CADASTRADO("Usuario já cadastrado com esse email."),
    USUARIO_COM_IDENTIFICADOR_CADASTRADO("Usuario já cadastrado com esse identificador."),
    ;

    private final String mensagem;
}
