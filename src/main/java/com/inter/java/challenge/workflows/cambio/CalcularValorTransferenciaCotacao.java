package com.inter.java.challenge.workflows.cambio;

import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalcularValorTransferenciaCotacao {

    private final CalculadoraCambio calculadoraCambio;

    public  ValoresTransferencia calcular(TransferirDinheiro model, CotacaoDolar cotacao) {
        return switch (model.moedaOrigem()) {
            case REAL -> new ValoresTransferencia(
                    model.valor(),
                    calculadoraCambio.converterRealParaDolar(
                            model.valor(),
                            cotacao.cotacaoCompra()
                    )
            );
            case DOLAR -> new ValoresTransferencia(
                    calculadoraCambio.converterDolarParaReal(
                            model.valor(),
                            cotacao.cotacaoCompra()
                    ),
                    model.valor()
            );
        };
    }
}
