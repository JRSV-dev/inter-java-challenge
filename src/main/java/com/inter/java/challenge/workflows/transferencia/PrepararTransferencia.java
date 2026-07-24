package com.inter.java.challenge.workflows.transferencia;

import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.data.records.TransferenciaPreparada;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import com.inter.java.challenge.workflows.buscar.buscarCotacao.BuscarCotacaoDolar;
import com.inter.java.challenge.workflows.cambio.CalcularValorTransferenciaCotacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PrepararTransferencia {

    private final BuscarCotacaoDolar buscarCotacaoDolar;
    private final CalcularValorTransferenciaCotacao calcularValores;
    private final Clock clock;

    public TransferenciaPreparada preparar(TransferirDinheiro comando) {
        LocalDate dataReferencia = LocalDate.now(clock);
        CotacaoDolar cotacao = buscarCotacaoDolar.buscar(dataReferencia);
        ValoresTransferencia valores = calcularValores.calcular(comando, cotacao);
        return new TransferenciaPreparada(comando, cotacao, valores);
    }
}
