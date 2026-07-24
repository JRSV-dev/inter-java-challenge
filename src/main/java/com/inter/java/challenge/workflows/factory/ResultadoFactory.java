package com.inter.java.challenge.workflows.factory;

import com.inter.java.challenge.data.model.Transferencia;
import com.inter.java.challenge.data.records.TransferenciaResultado;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResultadoFactory {

    public TransferenciaResultado criar(Transferencia transferencia) {

        log.info("Criando objeto de response.");
        return new TransferenciaResultado(
                transferencia.getId(),
                transferencia.getUsuarioOrigemId(),
                transferencia.getUsuarioDestinoId(),
                transferencia.getValorReal(),
                transferencia.getValorDolar(),
                transferencia.getCotacaoCompra(),
                transferencia.getDataCotacao(),
                transferencia.getDataTransferencia(),
                transferencia.getStatus()
        );
    }
}
