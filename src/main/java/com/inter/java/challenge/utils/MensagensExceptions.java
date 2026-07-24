package com.inter.java.challenge.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MensagensExceptions {

    USUARIO_NAO_ENCONTRADO("Usuário não encontrado."),
    USUARIO_COM_EMAIL_CADASTRADO("Usuario já cadastrado com esse email."),
    USUARIO_COM_IDENTIFICADOR_CADASTRADO("Usuario já cadastrado com esse identificador."),
    CARTEIRA_NAO_CADASTRADA_USUARIO("Esse id de carteira não pertence a nenhum usuario."),
    SALDO_INSUFICIENTE("Saldo insuficiente para fazer essa transferencia."),
    LIMITE_TRANSFERENCIA_EXCEDIDO("Limite diário exedido,tente novamente em outro horário amanhã."),
    COTACAO_INDISPONIVEL("Nenhuma cotação disponível foi encontrada.")
    ;

    private final String mensagem;
}
