package com.inter.java.challenge.workflows.validator.validarTransferencia.validacoes;

import com.inter.java.challenge.configuration.exception.exceptions.LimiteDiarioExcedidoException;
import com.inter.java.challenge.data.records.ContextoTransferencia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Order(2)
@Component
public class LimiteDiarioRegra implements RegraTransferencia<ContextoTransferencia> {

    @Override
    public void validar(ContextoTransferencia contexto) {

        log.info("Validando limite diário");

        BigDecimal limite = contexto.carteiraOrigem().getTipoUsuario().limiteDiario();

        BigDecimal totalProjetado = contexto.totalTransferidoHoje().add(contexto.valores().valorReal());

        if (totalProjetado.compareTo(limite) > 0) {
            log.info("Sem limite disponível.");
            throw new LimiteDiarioExcedidoException();
        }
    }
}
