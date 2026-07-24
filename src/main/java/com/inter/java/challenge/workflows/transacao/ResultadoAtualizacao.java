package com.inter.java.challenge.workflows.transacao;

import com.inter.java.challenge.configuration.exception.exceptions.AtualizacaoCarteiraInconsistenteException;

public final class ResultadoAtualizacao {

    private static final int QUANTIDADE_ESPERADA = 1;

    private ResultadoAtualizacao() {
    }

    public static void validar(int quantidadeAtualizada) {
        if (quantidadeAtualizada != QUANTIDADE_ESPERADA) {
            throw new AtualizacaoCarteiraInconsistenteException();
        }
    }
}
