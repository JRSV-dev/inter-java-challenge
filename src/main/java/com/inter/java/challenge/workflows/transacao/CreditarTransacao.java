package com.inter.java.challenge.workflows.transacao;

import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.data.records.ValoresTransferencia;
import com.inter.java.challenge.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditarTransacao {
    private final CarteiraRepository carteiraRepository;

    public void executar(TransferirDinheiro model, ValoresTransferencia valores) {
        log.info("Creditando saldo conta destino.");
        switch (model.moedaOrigem()) {
            case REAL -> carteiraRepository.creditarSaldoDolar(
                    model.usuarioDestinoId(),
                    valores.valorDolar()
            );
            case DOLAR -> carteiraRepository.creditarSaldoReal(
                    model.usuarioDestinoId(),
                    valores.valorReal()
            );
        }
    }
}
