package com.inter.java.challenge.workflows.transacao;

import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.inter.java.challenge.workflows.transacao.ResultadoAtualizacao.validar;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebitarTransacao implements Transacao {

    private final CarteiraRepository carteiraRepository;

    @Override
    public void executar(TransferirDinheiro model) {
        log.info("Debitando saldo devedor da conta origem.");
        int quantidadeAtualizada = switch (model.moedaOrigem()) {
            case REAL -> carteiraRepository.debitarSaldoReal(
                    model.usuarioOrigemId(),
                    model.valor()
            );
            case DOLAR -> carteiraRepository.debitarSaldoDolar(
                    model.usuarioOrigemId(),
                    model.valor()
            );
        };
        validar(quantidadeAtualizada);
    }
}
