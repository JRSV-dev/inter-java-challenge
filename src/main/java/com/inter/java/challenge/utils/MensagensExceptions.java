package com.inter.java.challenge.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MensagensExceptions {

    USUARIO_NAO_ENCONTRADO("Usuário não encontrado.");

    private final String mensagem;
}
