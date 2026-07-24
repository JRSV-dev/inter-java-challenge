package com.inter.java.challenge.workflows.transacao;

import com.inter.java.challenge.configuration.exception.exceptions.AtualizacaoCarteiraInconsistenteException;
import com.inter.java.challenge.data.records.TransferirDinheiro;
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
class DebitarTransacaoTest {

    @Mock
    private CarteiraRepository carteiraRepository;

    private DebitarTransacao debitarTransacao;

    @BeforeEach
    void setUp() {
        debitarTransacao = new DebitarTransacao(carteiraRepository);
    }

    @Test
    void deveDebitarSaldoEmRealQuandoMoedaDeOrigemForReal() {
        // Arrange
        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, REAL, new BigDecimal("100.00")
        );
        when(carteiraRepository.debitarSaldoReal(
                1L, new BigDecimal("100.00")
        )).thenReturn(1);

        // Act
        debitarTransacao.executar(comando);

        // Assert
        verify(carteiraRepository).debitarSaldoReal(
                1L, new BigDecimal("100.00")
        );
        verifyNoMoreInteractions(carteiraRepository);
    }

    @Test
    void deveDebitarSaldoEmDolarQuandoMoedaDeOrigemForDolar() {
        // Arrange
        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, DOLAR, new BigDecimal("20.0000")
        );
        when(carteiraRepository.debitarSaldoDolar(
                1L, new BigDecimal("20.0000")
        )).thenReturn(1);

        // Act
        debitarTransacao.executar(comando);

        // Assert
        verify(carteiraRepository).debitarSaldoDolar(
                1L, new BigDecimal("20.0000")
        );
        verifyNoMoreInteractions(carteiraRepository);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2})
    void deveFalharQuandoDebitoNaoAtualizarExatamenteUmaCarteira(
            int quantidadeAtualizada
    ) {
        TransferirDinheiro comando = new TransferirDinheiro(
                1L, 2L, REAL, new BigDecimal("100.00")
        );
        when(carteiraRepository.debitarSaldoReal(
                1L, new BigDecimal("100.00")
        )).thenReturn(quantidadeAtualizada);

        assertThatThrownBy(() -> debitarTransacao.executar(comando))
                .isInstanceOf(
                        AtualizacaoCarteiraInconsistenteException.class
                );
    }
}
