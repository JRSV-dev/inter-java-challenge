package com.inter.java.challenge.workflows.validator.validarTranferencia.validacoes;

import com.inter.java.challenge.configuration.exception.exceptions.SaldoInsuficienteException;
import com.inter.java.challenge.data.records.ContextoTransferencia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Order(1)
@Component
public class SaldoSuficienteRegra implements RegraTransferencia<ContextoTransferencia> {

    @Override
    public void validar(ContextoTransferencia contexto) {

        log.info("Validando saldo suficiente");
        BigDecimal saldoAtual = contexto.carteiraOrigem().getSaldoReal();
        BigDecimal valorTransferencia = contexto.model().valorReal();

        if (saldoAtual.compareTo(valorTransferencia) < 0) {
            throw new SaldoInsuficienteException();
        }
    }
}