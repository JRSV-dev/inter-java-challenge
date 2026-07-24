package com.inter.java.challenge.business;

import com.inter.java.challenge.api.model.TransferenciaRequest;
import com.inter.java.challenge.api.model.TransferenciaResponse;
import com.inter.java.challenge.data.records.TransferenciaPreparada;
import com.inter.java.challenge.data.records.TransferenciaResultado;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.mapper.TransferenciaMapper;
import com.inter.java.challenge.workflows.transferencia.ExecutarTransferencia;
import com.inter.java.challenge.workflows.transferencia.PrepararTransferencia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferenciaBusiness {

    private final TransferenciaMapper transferenciaMapper;
    private final PrepararTransferencia prepararTransferencia;
    private final ExecutarTransferencia executarTransferencia;

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest transferenciaRequest) {
        TransferirDinheiro comando = transferenciaMapper.requestParaModel(transferenciaRequest);
        TransferenciaPreparada transferencia = prepararTransferencia.preparar(comando);
        TransferenciaResultado resultado = executarTransferencia.executar(transferencia);
        return transferenciaMapper.resultadoParaResponse(resultado);
    }
}
