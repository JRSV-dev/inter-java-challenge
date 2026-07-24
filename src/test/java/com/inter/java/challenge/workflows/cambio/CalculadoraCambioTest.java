package com.inter.java.challenge.workflows.cambio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadoraCambioTest {

    private final CalculadoraCambio calculadoraCambio = new CalculadoraCambio();

    @Test
    void deveConverterRealParaDolar() {
        BigDecimal valorConvertido = calculadoraCambio.converterRealParaDolar(
                new BigDecimal("100.00"),
                new BigDecimal("5.0000")
        );

        assertThat(valorConvertido).isEqualByComparingTo("20.0000");
    }

    @Test
    void deveConverterDolarParaReal() {
        BigDecimal valorConvertido = calculadoraCambio.converterDolarParaReal(
                new BigDecimal("20.0000"),
                new BigDecimal("5.0000")
        );

        assertThat(valorConvertido).isEqualByComparingTo("100.00");
    }
}
