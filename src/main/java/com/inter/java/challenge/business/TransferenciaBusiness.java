package com.inter.java.challenge.business;


import com.inter.java.challenge.api.model.TransferenciaRequest;
import com.inter.java.challenge.api.model.TransferenciaResponse;
import com.inter.java.challenge.data.model.Transferencia;
import com.inter.java.challenge.data.records.*;
import com.inter.java.challenge.mapper.TransferenciaMapper;
import com.inter.java.challenge.repository.CarteiraRepository;
import com.inter.java.challenge.repository.TransferenciaRepository;
import com.inter.java.challenge.workflows.buscar.buscarCarteira.BuscarCarteira;
import com.inter.java.challenge.workflows.buscar.buscarCotacao.BuscarCotacaoDolar;
import com.inter.java.challenge.workflows.buscar.totalTransfereciaDia.ConsultarTotalTransferidoHoje;
import com.inter.java.challenge.workflows.cambio.CalculadoraCambio;
import com.inter.java.challenge.workflows.factory.ResultadoFactory;
import com.inter.java.challenge.workflows.factory.TransfereciaFactory;
import com.inter.java.challenge.workflows.validator.validarTranferencia.ValidarTransferencia;
import com.inter.java.challenge.workflows.validator.validarTranferencia.validacoes.RegraTransferencia;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDateTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferenciaBusiness {

    private final BuscarCotacaoDolar buscarCotacaoDolar;
    private final CalculadoraCambio calculadoraCambio;
    public final TransferenciaMapper transferenciaMapper;
    private final Clock clock;
    private final CarteiraRepository carteiraRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final TransfereciaFactory criarTransferenciaFactory;
    private final ResultadoFactory criarResultadoFactory;
    private final BuscarCarteira buscarCarteira;
    private final ConsultarTotalTransferidoHoje consultarTotalTransferidoHoje;
    private final ValidarTransferencia validarTransferencia;

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest transferenciaRequest) {
        TransferirDinheiro model = transferenciaMapper.requestParaModel(transferenciaRequest);
        LocalDate dataReferencia = LocalDate.now(clock);
        CotacaoDolar cotacao = buscarCotacaoDolar.buscar(dataReferencia);
        BigDecimal valorDolar = calculadoraCambio.converterRealParaDolar(model.valorReal(), cotacao.cotacaoCompra());
        TransferenciaResultado response = executar(model, cotacao, valorDolar);
        return transferenciaMapper.resultadoParaResponse(response);
    }


    public TransferenciaResultado executar(TransferirDinheiro model, CotacaoDolar cotacao, BigDecimal valorDolar) {
        CarteirasTransferencia carteiras = buscarCarteira.bloquear(model);
        BigDecimal totalTransferidoHoje = consultarTotalTransferidoHoje.consultar(model.usuarioOrigemId());
        ContextoTransferencia contexto = new ContextoTransferencia(model, carteiras.origem(), totalTransferidoHoje);
        validarTransferencia.validar(contexto);
        debitarOrigem(model);
        creditarDestino(model.usuarioDestinoId(), valorDolar);
        Transferencia transferencia = criarTransferenciaFactory.criar(model, cotacao, valorDolar, now());
        transferenciaRepository.salvarTransferencia(transferencia);
        return criarResultadoFactory.criar(transferencia);
    }


    private void debitarOrigem(TransferirDinheiro model) {

        log.info("Debitando saldo devedor da conta origem.");
        carteiraRepository.debitarSaldoReal(model.usuarioOrigemId(), model.valorReal());

    }

    private void creditarDestino(Long usuarioDestinoId, BigDecimal valorDolar) {

        log.info("Creditando saldo conta destino.");
       carteiraRepository.creditarSaldoDolar(usuarioDestinoId, valorDolar);
    }


}