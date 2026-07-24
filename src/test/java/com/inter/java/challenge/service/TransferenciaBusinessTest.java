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
import com.inter.java.challenge.mapper.TransferenciaMapper;
import com.inter.java.challenge.repository.TransferenciaRepository;
import com.inter.java.challenge.workflows.buscar.buscarCarteira.BuscarCarteira;
import com.inter.java.challenge.workflows.buscar.buscarCotacao.BuscarCotacaoDolar;
import com.inter.java.challenge.workflows.buscar.totalTransfereciaDia.ConsultarTotalTransferidoHoje;
import com.inter.java.challenge.workflows.cambio.CalculadoraCambio;
import com.inter.java.challenge.workflows.factory.ResultadoFactory;
import com.inter.java.challenge.workflows.factory.TransfereciaFactory;
import com.inter.java.challenge.workflows.transacao.CreditarTransacao;
import com.inter.java.challenge.workflows.transacao.DebitarTransacao;
import com.inter.java.challenge.workflows.validator.validarTranferencia.ValidarTransferencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferenciaBusinessTest {

    private static final Long USUARIO_ORIGEM_ID = 1L;
    private static final Long USUARIO_DESTINO_ID = 2L;

    private static final BigDecimal VALOR_REAL =
            new BigDecimal("100.00");

    private static final BigDecimal COTACAO_COMPRA =
            new BigDecimal("5.0000");

    private static final BigDecimal VALOR_DOLAR =
            new BigDecimal("20.0000");

    private static final BigDecimal TOTAL_TRANSFERIDO_HOJE =
            new BigDecimal("500.00");

    private static final ZoneId ZONA =
            ZoneId.of("America/Sao_Paulo");

    private static final Instant INSTANTE_FIXO =
            Instant.parse("2026-07-24T02:30:00Z");

    private static final Clock CLOCK_FIXO =
            Clock.fixed(INSTANTE_FIXO, ZONA);

    private static final LocalDate DATA_REFERENCIA =
            LocalDate.of(2026, 7, 23);

    private static final LocalDateTime DATA_TRANSFERENCIA =
            LocalDateTime.of(2026, 7, 23, 23, 30);

    @Mock
    private BuscarCotacaoDolar buscarCotacaoDolar;

    @Mock
    private CalculadoraCambio calculadoraCambio;

    @Mock
    private TransferenciaMapper transferenciaMapper;

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private TransfereciaFactory criarTransferenciaFactory;

    @Mock
    private ResultadoFactory criarResultadoFactory;

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

    @Mock
    private CarteiraTransferencia carteiraOrigem;

    @Mock
    private CarteiraTransferencia carteiraDestino;

    @Mock
    private Transferencia transferencia;

    @Mock
    private TransferenciaResultado resultado;

    private TransferenciaBusiness transferenciaBusiness;

    private TransferirDinheiro model;
    private CotacaoDolar cotacao;
    private CarteirasTransferencia carteiras;

    @BeforeEach
    void configurar() {
        model = new TransferirDinheiro(
                USUARIO_ORIGEM_ID,
                USUARIO_DESTINO_ID,
                VALOR_REAL,
                null
        );

        cotacao = new CotacaoDolar(
                COTACAO_COMPRA,
                DATA_REFERENCIA
        );

        carteiras = new CarteirasTransferencia(
                carteiraOrigem,
                carteiraDestino
        );

        transferenciaBusiness = new TransferenciaBusiness(
                buscarCotacaoDolar,
                calculadoraCambio,
                transferenciaMapper,
                CLOCK_FIXO,
                transferenciaRepository,
                criarTransferenciaFactory,
                criarResultadoFactory,
                buscarCarteira,
                consultarTotalTransferidoHoje,
                validarTransferencia,
                creditarTransacao,
                debitarTransacao
        );
    }

    @Test
    void deveRealizarTransferenciaComSucesso() {
        // Arrange
        prepararFluxoAteValidacao();

        when(criarTransferenciaFactory.criar(
                model,
                cotacao,
                VALOR_DOLAR,
                DATA_TRANSFERENCIA
        )).thenReturn(transferencia);

        when(criarResultadoFactory.criar(transferencia))
                .thenReturn(resultado);

        when(transferenciaMapper.resultadoParaResponse(resultado))
                .thenReturn(responseEsperada);

        // Act
        TransferenciaResponse response =
                transferenciaBusiness.transferir(request);

        // Assert
        assertThat(response).isSameAs(responseEsperada);

        InOrder ordem = inOrder(
                transferenciaMapper,
                buscarCotacaoDolar,
                calculadoraCambio,
                buscarCarteira,
                consultarTotalTransferidoHoje,
                validarTransferencia,
                debitarTransacao,
                creditarTransacao,
                criarTransferenciaFactory,
                transferenciaRepository,
                criarResultadoFactory
        );

        ordem.verify(transferenciaMapper)
                .requestParaModel(request);

        ordem.verify(buscarCotacaoDolar)
                .buscar(DATA_REFERENCIA);

        ordem.verify(calculadoraCambio)
                .converterRealParaDolar(
                        VALOR_REAL,
                        COTACAO_COMPRA
                );

        ordem.verify(buscarCarteira)
                .bloquear(model);

        ordem.verify(consultarTotalTransferidoHoje)
                .consultar(USUARIO_ORIGEM_ID);

        ordem.verify(validarTransferencia)
                .validar(any(ContextoTransferencia.class));

        ordem.verify(debitarTransacao)
                .executar(model);

        ordem.verify(creditarTransacao)
                .executar(model);

        ordem.verify(criarTransferenciaFactory)
                .criar(
                        model,
                        cotacao,
                        VALOR_DOLAR,
                        DATA_TRANSFERENCIA
                );

        ordem.verify(transferenciaRepository)
                .salvarTransferencia(transferencia);

        ordem.verify(criarResultadoFactory)
                .criar(transferencia);

        verify(transferenciaMapper)
                .resultadoParaResponse(resultado);
    }

    @Test
    void deveMontarContextoCorretamenteAntesDeValidar() {
        // Arrange
        prepararFluxoAteValidacao();

        when(criarTransferenciaFactory.criar(
                model,
                cotacao,
                VALOR_DOLAR,
                DATA_TRANSFERENCIA
        )).thenReturn(transferencia);

        when(criarResultadoFactory.criar(transferencia))
                .thenReturn(resultado);

        when(transferenciaMapper.resultadoParaResponse(resultado))
                .thenReturn(responseEsperada);

        ArgumentCaptor<ContextoTransferencia> captor =
                ArgumentCaptor.forClass(ContextoTransferencia.class);

        // Act
        transferenciaBusiness.transferir(request);

        // Assert
        verify(validarTransferencia).validar(captor.capture());

        ContextoTransferencia contexto = captor.getValue();

        assertThat(contexto.model()).isSameAs(model);
        assertThat(contexto.carteiraOrigem())
                .isSameAs(carteiraOrigem);

        assertThat(contexto.totalTransferidoHoje())
                .isEqualByComparingTo(TOTAL_TRANSFERIDO_HOJE);
    }

    @Test
    void naoDeveMovimentarSaldoQuandoValidacaoFalhar() {
        // Arrange
        prepararFluxoAteValidacao();

        RuntimeException erro =
                new RuntimeException(
                        "Regra de transferência inválida"
                );

        doThrow(erro)
                .when(validarTransferencia)
                .validar(any(ContextoTransferencia.class));

        // Act e Assert
        assertThatThrownBy(
                () -> transferenciaBusiness.transferir(request)
        ).isSameAs(erro);

        verifyNoInteractions(
                debitarTransacao,
                creditarTransacao,
                criarTransferenciaFactory,
                transferenciaRepository,
                criarResultadoFactory
        );

        verify(transferenciaMapper, never())
                .resultadoParaResponse(
                        any(TransferenciaResultado.class)
                );
    }

    @Test
    void naoDeveContinuarQuandoBuscaDaCotacaoFalhar() {
        // Arrange
        RuntimeException erro =
                new RuntimeException("Cotação indisponível");

        when(transferenciaMapper.requestParaModel(request))
                .thenReturn(model);

        when(buscarCotacaoDolar.buscar(DATA_REFERENCIA))
                .thenThrow(erro);

        // Act e Assert
        assertThatThrownBy(
                () -> transferenciaBusiness.transferir(request)
        ).isSameAs(erro);

        verifyNoInteractions(
                calculadoraCambio,
                buscarCarteira,
                consultarTotalTransferidoHoje,
                validarTransferencia,
                debitarTransacao,
                creditarTransacao,
                transferenciaRepository,
                criarTransferenciaFactory,
                criarResultadoFactory
        );

        verify(transferenciaMapper, never())
                .resultadoParaResponse(
                        any(TransferenciaResultado.class)
                );
    }

    @Test
    void naoDeveContinuarQuandoConversaoFalhar() {
        // Arrange
        when(transferenciaMapper.requestParaModel(request))
                .thenReturn(model);

        when(buscarCotacaoDolar.buscar(DATA_REFERENCIA))
                .thenReturn(cotacao);

        RuntimeException erro =
                new RuntimeException("Erro ao converter moeda");

        when(calculadoraCambio.converterRealParaDolar(
                VALOR_REAL,
                COTACAO_COMPRA
        )).thenThrow(erro);

        // Act e Assert
        assertThatThrownBy(
                () -> transferenciaBusiness.transferir(request)
        ).isSameAs(erro);

        verifyNoInteractions(
                buscarCarteira,
                consultarTotalTransferidoHoje,
                validarTransferencia,
                debitarTransacao,
                creditarTransacao,
                transferenciaRepository,
                criarTransferenciaFactory,
                criarResultadoFactory
        );
    }

    @Test
    void naoDeveCreditarQuandoDebitoFalhar() {
        // Arrange
        prepararFluxoAteValidacao();

        RuntimeException erro =
                new RuntimeException(
                        "Não foi possível debitar a origem"
                );

        doThrow(erro)
                .when(debitarTransacao)
                .executar(model);

        // Act e Assert
        assertThatThrownBy(
                () -> transferenciaBusiness.transferir(request)
        ).isSameAs(erro);


        verifyNoInteractions(
                criarTransferenciaFactory,
                transferenciaRepository,
                criarResultadoFactory
        );
    }

    @Test
    void naoDeveSalvarTransferenciaQuandoCreditoFalhar() {
        // Arrange
        prepararFluxoAteValidacao();

        RuntimeException erro =
                new RuntimeException(
                        "Não foi possível creditar o destino"
                );

        doThrow(erro)
                .when(creditarTransacao)
                .executar(model);

        // Act e Assert
        assertThatThrownBy(
                () -> transferenciaBusiness.transferir(request)
        ).isSameAs(erro);

        verify(debitarTransacao).executar(model);

        verifyNoInteractions(
                criarTransferenciaFactory,
                transferenciaRepository,
                criarResultadoFactory
        );
    }

    @Test
    void devePropagarErroQuandoPersistenciaFalhar() {
        // Arrange
        prepararFluxoAteValidacao();

        when(criarTransferenciaFactory.criar(
                model,
                cotacao,
                VALOR_DOLAR,
                DATA_TRANSFERENCIA
        )).thenReturn(transferencia);

        RuntimeException erro =
                new RuntimeException(
                        "Não foi possível salvar a transferência"
                );

        doThrow(erro)
                .when(transferenciaRepository)
                .salvarTransferencia(transferencia);

        // Act e Assert
        assertThatThrownBy(
                () -> transferenciaBusiness.transferir(request)
        ).isSameAs(erro);

        verify(debitarTransacao).executar(model);

        verify(creditarTransacao).executar(model);

        verifyNoInteractions(criarResultadoFactory);

        verify(transferenciaMapper, never())
                .resultadoParaResponse(
                        any(TransferenciaResultado.class)
                );
    }

    private void prepararFluxoAteValidacao() {
        when(transferenciaMapper.requestParaModel(request))
                .thenReturn(model);

        when(buscarCotacaoDolar.buscar(DATA_REFERENCIA))
                .thenReturn(cotacao);

        when(calculadoraCambio.converterRealParaDolar(
                VALOR_REAL,
                COTACAO_COMPRA
        )).thenReturn(VALOR_DOLAR);

        when(buscarCarteira.bloquear(model))
                .thenReturn(carteiras);

        when(consultarTotalTransferidoHoje.consultar(
                USUARIO_ORIGEM_ID
        )).thenReturn(TOTAL_TRANSFERIDO_HOJE);
    }
}