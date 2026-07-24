package com.inter.java.challenge.workflows.transferencia;

import com.inter.java.challenge.data.records.TransferenciaPreparada;
import com.inter.java.challenge.data.records.TransferenciaResultado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExecutarTransferencia {

    private final ValidarContextoTransferencia validarContexto;
    private final MovimentarSaldo movimentarSaldo;
    private final ConcluirTransferencia concluirTransferencia;

    public TransferenciaResultado executar(TransferenciaPreparada transferencia) {
        validarContexto.validar(transferencia);
        movimentarSaldo.executar(transferencia);
        return concluirTransferencia.concluir(transferencia);
    }
}
