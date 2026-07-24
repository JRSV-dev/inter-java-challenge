package com.inter.java.challenge.workflows.factory;

import com.inter.java.challenge.data.enums.MoedaOrigem;
import com.inter.java.challenge.data.enums.StatusTransferencia;
import com.inter.java.challenge.data.model.Transferencia;
import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransferenciaFactoryTest {

    private final TransferenciaFactory factory = new TransferenciaFactory();

    @Test
    void deveCriarHistoricoComMoedaValoresECotacaoDaOperacao() {
        TransferirDinheiro comando = new TransferirDinheiro(
                1L,
                2L,
                MoedaOrigem.DOLAR,
                new BigDecimal("20.1234")
        );
        CotacaoDolar cotacao = new CotacaoDolar(
                new BigDecimal("5.4321"),
                LocalDate.of(2026, 7, 23)
        );
        ValoresTransferencia valores = new ValoresTransferencia(
                new BigDecimal("109.31"),
                new BigDecimal("20.1234")
        );
        LocalDateTime dataTransferencia =
                LocalDateTime.of(2026, 7, 24, 10, 30);

        Transferencia transferencia =
                factory.criar(comando, cotacao, valores, dataTransferencia);

        assertThat(transferencia.getUsuarioOrigemId()).isEqualTo(1L);
        assertThat(transferencia.getUsuarioDestinoId()).isEqualTo(2L);
        assertThat(transferencia.getMoedaOrigem()).isEqualTo(MoedaOrigem.DOLAR);
        assertThat(transferencia.getValorReal()).isEqualByComparingTo("109.31");
        assertThat(transferencia.getValorDolar()).isEqualByComparingTo("20.1234");
        assertThat(transferencia.getCotacaoCompra()).isEqualByComparingTo("5.4321");
        assertThat(transferencia.getDataCotacao())
                .isEqualTo(LocalDate.of(2026, 7, 23));
        assertThat(transferencia.getDataTransferencia())
                .isEqualTo(dataTransferencia);
        assertThat(transferencia.getStatus())
                .isEqualTo(StatusTransferencia.CONCLUIDA);
    }
}
