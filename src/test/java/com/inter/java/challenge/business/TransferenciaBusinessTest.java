package com.inter.java.challenge.business;

import com.inter.java.challenge.api.model.TransferenciaRequest;
import com.inter.java.challenge.api.model.TransferenciaResponse;
import com.inter.java.challenge.data.model.CarteiraTransferencia;
import com.inter.java.challenge.data.model.Transferencia;
import com.inter.java.challenge.data.records.CarteirasTransferencia;
import com.inter.java.challenge.data.records.ContextoTransferencia;
import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.data.records.TransferenciaResultado;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import com.inter.java.challenge.mapper.TransferenciaMapper;
import com.inter.java.challenge.repository.TransferenciaRepository;
import com.inter.java.challenge.workflows.buscar.buscarCarteira.BuscarCarteira;
import com.inter.java.challenge.workflows.buscar.buscarCotacao.BuscarCotacaoDolar;
import com.inter.java.challenge.workflows.buscar.totalTransferenciaDia.ConsultarTotalTransferidoHoje;
import com.inter.java.challenge.workflows.cambio.CalcularValorTransferenciaCotacao;
import com.inter.java.challenge.workflows.factory.ResultadoFactory;
import com.inter.java.challenge.workflows.factory.TransferenciaFactory;
import com.inter.java.challenge.workflows.transacao.CreditarTransacao;
import com.inter.java.challenge.workflows.transacao.DebitarTransacao;
import com.inter.java.challenge.workflows.validator.validarTransferencia.ValidarTransferencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.inter.java.challenge.data.enums.MoedaOrigem.DOLAR;
import static com.inter.java.challenge.data.enums.MoedaOrigem.REAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferenciaBusinessTest {

    private static final long ORIGEM_ID = 1L;
    private static final long DESTINO_ID = 2L;
    private static final BigDecimal VALOR_REAL = new BigDecimal("100.00");
    private static final BigDecimal VALOR_DOLAR = new BigDecimal("20.0000");
    private static final BigDecimal COTACAO = new BigDecimal("5.0000");
    private static final LocalDate DATA_COTACAO = LocalDate.of(2026, 7, 23);
    private static final LocalDate DATA_REFERENCIA = LocalDate.of(2026, 7, 23);
    private static final LocalDateTime DATA_TRANSFERENCIA =
            LocalDateTime.of(2026, 7, 23, 23, 30);
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-07-24T02:30:00Z"),
            ZoneId.of("America/Sao_Paulo")
    );

    @Mock
    private BuscarCotacaoDolar buscarCotacaoDolar;
    @Mock
    private CalcularValorTransferenciaCotacao calcularValores;
    @Mock
    private TransferenciaMapper transferenciaMapper;
    @Mock
    private TransferenciaRepository transferenciaRepository;
    @Mock
    private TransferenciaFactory transferenciaFactory;
    @Mock
    private ResultadoFactory resultadoFactory;
    @Mock
    private BuscarCarteira buscarCarteira;
    @Mock
    private ConsultarTotalTransferidoHoje consultarTotalTransferidoHoje;
    @Mock
    private ValidarTransferencia validarTransferencia;
    @Mock
    private CreditarTransacao creditarTransacao;
    @Mock
    private DebitarTransacao debitarTransacao;
    @Mock
    private TransferenciaRequest request;
    @Mock
    private TransferenciaResponse responseEsperada;

    private TransferenciaBusiness transferenciaBusiness;
    private CotacaoDolar cotacao;
    private CarteiraTransferencia carteiraOrigem;
    private CarteirasTransferencia carteiras;

    @BeforeEach
    void setUp() {
        cotacao = new CotacaoDolar(COTACAO, DATA_COTACAO);
        carteiraOrigem = carteira(ORIGEM_ID, "1000.00", "200.0000");
        carteiras = new CarteirasTransferencia(
                carteiraOrigem,
                carteira(DESTINO_ID, "1000.00", "200.0000")
        );

        transferenciaBusiness = new TransferenciaBusiness(
                buscarCotacaoDolar,
                transferenciaMapper,
                CLOCK,
                transferenciaRepository,
                transferenciaFactory,
                resultadoFactory,
                buscarCarteira,
                consultarTotalTransferidoHoje,
                validarTransferencia,
                creditarTransacao,
                debitarTransacao,
                calcularValores
        );
    }

    @Nested
    class TransferenciasConcluidas {

        @Test
        void deveDebitarRealECreditarDolarConvertido() {
            // Arrange
            TransferirDinheiro comando = new TransferirDinheiro(
                    ORIGEM_ID, DESTINO_ID, REAL, VALOR_REAL
            );
            ValoresTransferencia valoresEsperados =
                    new ValoresTransferencia(VALOR_REAL, VALOR_DOLAR);
            prepararFluxoComum(comando);

            // Act
            TransferenciaResponse resultado = transferenciaBusiness.transferir(request);

            // Assert
            assertThat(resultado).isSameAs(responseEsperada);
            verify(debitarTransacao).executar(comando);
            verify(creditarTransacao).executar(comando, valoresEsperados);
            verify(transferenciaFactory).criar(
                    comando, cotacao, valoresEsperados, DATA_TRANSFERENCIA
            );
            verify(calcularValores).calcular(comando, cotacao);
            assertContextoValidado(comando, valoresEsperados);
        }

        @Test
        void deveDebitarDolarECreditarRealConvertido() {
            // Arrange
            TransferirDinheiro comando = new TransferirDinheiro(
                    ORIGEM_ID, DESTINO_ID, DOLAR, VALOR_DOLAR
            );
            ValoresTransferencia valoresEsperados =
                    new ValoresTransferencia(VALOR_REAL, VALOR_DOLAR);
            prepararFluxoComum(comando);

            // Act
            TransferenciaResponse resultado = transferenciaBusiness.transferir(request);

            // Assert
            assertThat(resultado).isSameAs(responseEsperada);
            verify(debitarTransacao).executar(comando);
            verify(creditarTransacao).executar(comando, valoresEsperados);
            verify(transferenciaFactory).criar(
                    comando, cotacao, valoresEsperados, DATA_TRANSFERENCIA
            );
            verify(calcularValores).calcular(comando, cotacao);
            assertContextoValidado(comando, valoresEsperados);
        }
    }

    @Nested
    class Falhas {

        @Test
        void naoDeveMovimentarSaldosQuandoValidacaoFalhar() {
            // Arrange
            TransferirDinheiro comando = new TransferirDinheiro(
                    ORIGEM_ID, DESTINO_ID, REAL, VALOR_REAL
            );
            prepararAteValidacao(comando);
            RuntimeException erro = new RuntimeException("Transferência inválida");
            doThrow(erro).when(validarTransferencia)
                    .validar(any(ContextoTransferencia.class));

            // Act / Assert
            assertThatThrownBy(() -> transferenciaBusiness.transferir(request))
                    .isSameAs(erro);
            verifyNoInteractions(
                    debitarTransacao,
                    creditarTransacao,
                    transferenciaFactory,
                    transferenciaRepository,
                    resultadoFactory
            );
        }

        @Test
        void naoDeveCreditarNemPersistirQuandoDebitoFalhar() {
            // Arrange
            TransferirDinheiro comando = new TransferirDinheiro(
                    ORIGEM_ID, DESTINO_ID, REAL, VALOR_REAL
            );
            prepararAteValidacao(comando);
            RuntimeException erro = new RuntimeException("Falha no débito");
            doThrow(erro).when(debitarTransacao).executar(comando);

            // Act / Assert
            assertThatThrownBy(() -> transferenciaBusiness.transferir(request))
                    .isSameAs(erro);
            verifyNoInteractions(
                    creditarTransacao,
                    transferenciaFactory,
                    transferenciaRepository,
                    resultadoFactory
            );
        }

        @Test
        void naoDevePersistirQuandoCreditoFalhar() {
            // Arrange
            TransferirDinheiro comando = new TransferirDinheiro(
                    ORIGEM_ID, DESTINO_ID, REAL, VALOR_REAL
            );
            ValoresTransferencia valores =
                    new ValoresTransferencia(VALOR_REAL, VALOR_DOLAR);
            prepararAteValidacao(comando);
            RuntimeException erro = new RuntimeException("Falha no crédito");
            doThrow(erro).when(creditarTransacao).executar(comando, valores);

            // Act / Assert
            assertThatThrownBy(() -> transferenciaBusiness.transferir(request))
                    .isSameAs(erro);
            verify(debitarTransacao).executar(comando);
            verifyNoInteractions(
                    transferenciaFactory,
                    transferenciaRepository,
                    resultadoFactory
            );
        }
    }

    private void prepararFluxoComum(TransferirDinheiro comando) {
        prepararAteValidacao(comando);
        Transferencia transferencia = new Transferencia();
        TransferenciaResultado resultado = org.mockito.Mockito.mock(
                TransferenciaResultado.class
        );
        when(transferenciaFactory.criar(
                any(), any(), any(), any(LocalDateTime.class)
        )).thenReturn(transferencia);
        when(resultadoFactory.criar(transferencia)).thenReturn(resultado);
        when(transferenciaMapper.resultadoParaResponse(resultado))
                .thenReturn(responseEsperada);
    }

    private void prepararAteValidacao(TransferirDinheiro comando) {
        when(transferenciaMapper.requestParaModel(request)).thenReturn(comando);
        when(buscarCotacaoDolar.buscar(DATA_REFERENCIA)).thenReturn(cotacao);
        when(calcularValores.calcular(comando, cotacao))
                .thenReturn(new ValoresTransferencia(
                        VALOR_REAL,
                        VALOR_DOLAR
                ));
        when(buscarCarteira.bloquear(comando)).thenReturn(carteiras);
        when(consultarTotalTransferidoHoje.consultar(ORIGEM_ID))
                .thenReturn(BigDecimal.ZERO);
    }

    private void assertContextoValidado(
            TransferirDinheiro comando,
            ValoresTransferencia valores
    ) {
        ArgumentCaptor<ContextoTransferencia> captor =
                ArgumentCaptor.forClass(ContextoTransferencia.class);
        verify(validarTransferencia).validar(captor.capture());
        assertThat(captor.getValue())
                .extracting(
                        ContextoTransferencia::model,
                        ContextoTransferencia::valores,
                        ContextoTransferencia::carteiraOrigem,
                        ContextoTransferencia::totalTransferidoHoje
                )
                .containsExactly(
                        comando,
                        valores,
                        carteiraOrigem,
                        BigDecimal.ZERO
                );
    }

    private CarteiraTransferencia carteira(
            long usuarioId,
            String saldoReal,
            String saldoDolar
    ) {
        CarteiraTransferencia carteira = new CarteiraTransferencia();
        carteira.setUsuarioId(usuarioId);
        carteira.setSaldoReal(new BigDecimal(saldoReal));
        carteira.setSaldoDolar(new BigDecimal(saldoDolar));
        return carteira;
    }
}
