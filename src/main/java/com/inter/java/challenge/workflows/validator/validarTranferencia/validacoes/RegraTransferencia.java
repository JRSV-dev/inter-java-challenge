package com.inter.java.challenge.workflows.validator.validarTranferencia.validacoes;

import com.inter.java.challenge.data.records.ContextoTransferencia;

public interface RegraTransferencia<T> {

    void validar(T objeto);
}