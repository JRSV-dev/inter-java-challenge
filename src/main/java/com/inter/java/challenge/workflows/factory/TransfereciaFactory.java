package com.inter.java.challenge.workflows.factory;

import com.inter.java.challenge.data.model.Transferencia;
import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.inter.java.challenge.data.enums.StatusTransferencia.CONCLUIDA;

@Slf4j
@Component
public class TransfereciaFactory {

    public Transferencia criar(
            TransferirDinheiro comando,
            CotacaoDolar cotacao,
            ValoresTransferencia valores,
            LocalDateTime agora
    ) {

        log.info("Criando registro de transferencia.");
        Transferencia transferencia = new Transferencia();
        transferencia.setUsuarioOrigemId(comando.usuarioOrigemId());
        transferencia.setUsuarioDestinoId(comando.usuarioDestinoId());
        transferencia.setMoedaOrigem(comando.moedaOrigem());
        transferencia.setValorReal(valores.valorReal());
        transferencia.setValorDolar(valores.valorDolar());
        transferencia.setCotacaoCompra(cotacao.cotacaoCompra());
        transferencia.setDataCotacao(cotacao.dataCotacao());
        transferencia.setDataTransferencia(agora);
        transferencia.setStatus(CONCLUIDA);
        return transferencia;
    }
}
