package com.inter.java.challenge.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MensagensExceptions {

    USUARIO_NAO_ENCONTRADO("Usuário não encontrado."),
    USUARIO_COM_EMAIL_CADASTRADO("Usuário já cadastrado com este e-mail."),
    USUARIO_COM_IDENTIFICADOR_CADASTRADO("Usuário já cadastrado com este identificador."),
    CARTEIRA_NAO_CADASTRADA_USUARIO("Nenhuma carteira foi encontrada para o usuário informado."),
    SALDO_INSUFICIENTE("Saldo insuficiente para realizar a transferência."),
    LIMITE_TRANSFERENCIA_EXCEDIDO("Limite diário de transferência excedido."),
    COTACAO_INDISPONIVEL("Nenhuma cotação disponível foi encontrada.")
    ;

    private final String mensagem;
}
