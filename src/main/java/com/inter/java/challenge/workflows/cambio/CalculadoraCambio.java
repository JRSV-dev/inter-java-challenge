package com.inter.java.challenge.workflows.cambio;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.inter.java.challenge.utils.ValoresPadrao.CASAS_DECIMAIS_DOLAR;
import static com.inter.java.challenge.utils.ValoresPadrao.CASAS_DECIMAIS_REAL;

@Slf4j
@Component
public class CalculadoraCambio {

    public BigDecimal converterRealParaDolar(BigDecimal valorReal, BigDecimal cotacaoCompra) {
        log.info("Calculando conversão de real para dólar.");
        return valorReal.divide(
                cotacaoCompra,
                CASAS_DECIMAIS_DOLAR,
                RoundingMode.HALF_EVEN
        );
    }

    public BigDecimal converterDolarParaReal(BigDecimal valorDolar, BigDecimal cotacaoCompra) {
        log.info("Calculando conversão de dólar para real.");
        return valorDolar
                .multiply(cotacaoCompra)
                .setScale(CASAS_DECIMAIS_REAL, RoundingMode.HALF_EVEN);
    }
}
