package com.inter.java.challenge.workflows.transacao;

import com.inter.java.challenge.configuration.exception.exceptions.AtualizacaoCarteiraInconsistenteException;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import com.inter.java.challenge.repository.CarteiraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static com.inter.java.challenge.data.enums.MoedaOrigem.DOLAR;
import static com.inter.java.challenge.data.enums.MoedaOrigem.REAL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditarTransacaoTest {

    private static final ValoresTransferencia VALORES =
            new ValoresTransferencia(
                    new BigDecimal("100.00"),
                    new BigDecimal("20.0000")
            );

    @Mock
    private CarteiraRepository carteiraRepository;

    private CreditarTransacao creditarTransacao;

    @BeforeEach
    void setUp() {
        creditarTransacao = new CreditarTransacao(carteiraRepository);
    }

    @Test
    void deveCreditarValorConvertidoEmDolarQuandoOrigemForReal() {
        // Arrange
        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, REAL, new BigDecimal("100.00")
        );
        when(carteiraRepository.creditarSaldoDolar(
                2L, new BigDecimal("20.0000")
        )).thenReturn(1);

        // Act
        creditarTransacao.executar(comando, VALORES);

        // Assert
        verify(carteiraRepository).creditarSaldoDolar(
                2L, new BigDecimal("20.0000")
        );
        verifyNoMoreInteractions(carteiraRepository);
    }

    @Test
    void deveCreditarValorConvertidoEmRealQuandoOrigemForDolar() {
        // Arrange
        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, DOLAR, new BigDecimal("20.0000")
        );
        when(carteiraRepository.creditarSaldoReal(
                2L, new BigDecimal("100.00")
        )).thenReturn(1);

        // Act
        creditarTransacao.executar(comando, VALORES);

        // Assert
        verify(carteiraRepository).creditarSaldoReal(
                2L, new BigDecimal("100.00")
        );
        verifyNoMoreInteractions(carteiraRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2})
    void deveFalharQuandoCreditoNaoAtualizarExatamenteUmaCarteira(
            int quantidadeAtualizada
    ) {
        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, REAL, new BigDecimal("100.00")
        );
        when(carteiraRepository.creditarSaldoDolar(
                2L, new BigDecimal("20.0000")
        )).thenReturn(quantidadeAtualizada);

        assertThatThrownBy(() ->
                creditarTransacao.executar(comando, VALORES)
        )
                .isInstanceOf(
                        AtualizacaoCarteiraInconsistenteException.class
                );
    }
}
