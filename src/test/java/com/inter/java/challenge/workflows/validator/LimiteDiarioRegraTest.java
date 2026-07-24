package com.inter.java.challenge.workflows.validator;

import com.inter.java.challenge.configuration.exception.exceptions.LimiteDiarioExcedidoException;
import com.inter.java.challenge.data.model.CarteiraTransferencia;
import com.inter.java.challenge.data.records.ContextoTransferencia;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import com.inter.java.challenge.workflows.validator.validarTransferencia.validacoes.LimiteDiarioRegra;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.inter.java.challenge.data.enums.MoedaOrigem.DOLAR;
import static com.inter.java.challenge.data.enums.TipoUsuario.PF;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LimiteDiarioRegraTest {

    private final LimiteDiarioRegra regra = new LimiteDiarioRegra();

    @Test
    void deveConsiderarEquivalenteEmRealNoLimiteDiario() {
        ContextoTransferencia contexto = contexto("500.00", "9500.00");

        assertThatCode(() -> regra.validar(contexto))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarQuandoEquivalenteEmRealExcederLimiteDiario() {
        ContextoTransferencia contexto = contexto("500.01", "9500.00");

        assertThatThrownBy(() -> regra.validar(contexto))
                .isInstanceOf(LimiteDiarioExcedidoException.class);
    }

    private ContextoTransferencia contexto(
            String valorReal,
            String totalTransferido
    ) {
        CarteiraTransferencia carteira = new CarteiraTransferencia();
        carteira.setUsuarioId(1L);
        carteira.setTipoUsuario(PF);
        carteira.setSaldoReal(new BigDecimal("20000.00"));
        carteira.setSaldoDolar(new BigDecimal("4000.0000"));

        return new ContextoTransferencia(
                new TransferirDinheiro(
                        1L, 2L, DOLAR, new BigDecimal("100.0000")
                ),
                new ValoresTransferencia(
                        new BigDecimal(valorReal), new BigDecimal("100.0000")
                ),
                carteira,
                new BigDecimal(totalTransferido)
        );
    }
}
