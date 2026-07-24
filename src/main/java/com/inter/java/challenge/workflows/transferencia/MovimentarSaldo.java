package com.inter.java.challenge.workflows.transferencia;

import com.inter.java.challenge.data.records.TransferenciaPreparada;
import com.inter.java.challenge.workflows.transacao.CreditarTransacao;
import com.inter.java.challenge.workflows.transacao.DebitarTransacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovimentarSaldo {

    private final DebitarTransacao debitarTransacao;
    private final CreditarTransacao creditarTransacao;

    public void executar(TransferenciaPreparada transferencia) {
        debitarTransacao.executar(transferencia.comando());
        creditarTransacao.executar(transferencia.comando(), transferencia.valores());
    }
}
