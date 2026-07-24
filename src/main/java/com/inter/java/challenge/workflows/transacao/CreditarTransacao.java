package com.inter.java.challenge.workflows.transacao;

import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditarTransacao implements Transacao{
    private final CarteiraRepository carteiraRepository;

    @Override
    public void executar(TransferirDinheiro model) {
        log.info("Creditando saldo conta destino.");
        carteiraRepository.creditarSaldoDolar(model.usuarioOrigemId(), model.valorReal());
    }
}
