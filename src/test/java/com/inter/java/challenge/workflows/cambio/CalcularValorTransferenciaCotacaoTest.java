package com.inter.java.challenge.workflows.cambio;

import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.inter.java.challenge.data.enums.MoedaOrigem.DOLAR;
import static com.inter.java.challenge.data.enums.MoedaOrigem.REAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularValorTransferenciaCotacaoTest {

    private static final BigDecimal VALOR_REAL = new BigDecimal("100.00");
    private static final BigDecimal VALOR_DOLAR = new BigDecimal("20.0000");
    private static final BigDecimal COTACAO = new BigDecimal("5.0000");

    @Mock
    private CalculadoraCambio calculadoraCambio;

    private CalcularValorTransferenciaCotacao calcularValores;
    private CotacaoDolar cotacao;

    @BeforeEach
    void setUp() {
        calcularValores =
                new CalcularValorTransferenciaCotacao(calculadoraCambio);
        cotacao = new CotacaoDolar(COTACAO, LocalDate.of(2026, 7, 23));
    }

    @Test
    void deveManterValorRealECalcularValorDolar() {
        // Arrange
        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, REAL, VALOR_REAL
        );
        when(calculadoraCambio.converterRealParaDolar(VALOR_REAL, COTACAO))
                .thenReturn(VALOR_DOLAR);

        // Act
        ValoresTransferencia resultado =
                calcularValores.calcular(comando, cotacao);

        // Assert
        assertThat(resultado).isEqualTo(
                new ValoresTransferencia(VALOR_REAL, VALOR_DOLAR)
        );
        verify(calculadoraCambio)
                .converterRealParaDolar(VALOR_REAL, COTACAO);
        verifyNoMoreInteractions(calculadoraCambio);
    }

    @Test
    void deveManterValorDolarECalcularValorReal() {
        // Arrange
        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, DOLAR, VALOR_DOLAR
        );
        when(calculadoraCambio.converterDolarParaReal(VALOR_DOLAR, COTACAO))
                .thenReturn(VALOR_REAL);

        // Act
        ValoresTransferencia resultado =
                calcularValores.calcular(comando, cotacao);

        // Assert
        assertThat(resultado).isEqualTo(
                new ValoresTransferencia(VALOR_REAL, VALOR_DOLAR)
        );
        verify(calculadoraCambio)
                .converterDolarParaReal(VALOR_DOLAR, COTACAO);
        verifyNoMoreInteractions(calculadoraCambio);
    }
}
