package com.inter.java.challenge.workflows.validator;

import com.inter.java.challenge.configuration.exception.exceptions.SaldoInsuficienteException;
import com.inter.java.challenge.data.model.CarteiraTransferencia;
import com.inter.java.challenge.data.records.ContextoTransferencia;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import com.inter.java.challenge.workflows.validator.validarTranferencia.validacoes.SaldoSuficienteRegra;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.inter.java.challenge.data.enums.MoedaOrigem.DOLAR;
import static com.inter.java.challenge.data.enums.MoedaOrigem.REAL;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SaldoSuficienteRegraTest {

    private final SaldoSuficienteRegra regra = new SaldoSuficienteRegra();

    @Test
    void deveValidarSaldoEmRealQuandoOrigemForReal() {
        ContextoTransferencia contexto = contexto(
                REAL, "100.00", "100.00", "0.0000"
        );

        assertThatCode(() -> regra.validar(contexto))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarTransferenciaQuandoSaldoEmDolarForInsuficiente() {
        ContextoTransferencia contexto = contexto(
                DOLAR, "20.0000", "1000.00", "19.9999"
        );

        assertThatThrownBy(() -> regra.validar(contexto))
                .isInstanceOf(SaldoInsuficienteException.class);
    }

    private ContextoTransferencia contexto(
            com.inter.java.challenge.data.enums.MoedaOrigem moeda,
            String valor,
            String saldoReal,
            String saldoDolar
    ) {
        CarteiraTransferencia carteira = new CarteiraTransferencia();
        carteira.setUsuarioId(1L);
        carteira.setSaldoReal(new BigDecimal(saldoReal));
        carteira.setSaldoDolar(new BigDecimal(saldoDolar));

        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, moeda, new BigDecimal(valor)
        );
        ValoresTransferencia valores = moeda == REAL
                ? new ValoresTransferencia(
                        new BigDecimal(valor), new BigDecimal("20.0000")
                )
                : new ValoresTransferencia(
                        new BigDecimal("100.00"), new BigDecimal(valor)
                );
        return new ContextoTransferencia(
                comando, valores, carteira, BigDecimal.ZERO
        );
    }
}
