package com.inter.java.challenge.workflows.buscar.buscarCarteira;

import com.inter.java.challenge.configuration.exception.exceptions.CarteiraNaoEncontrada;
import com.inter.java.challenge.data.model.Carteira;
import com.inter.java.challenge.data.model.CarteiraTransferencia;
import com.inter.java.challenge.data.records.CarteirasTransferencia;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import com.inter.java.challenge.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuscarCarteira {

    private final CarteiraRepository carteiraRepository;

    public Carteira buscarCarteiraPeloUsuarioId(Long usuarioId){
        return carteiraRepository.buscarPorUsuarioId(usuarioId).orElseThrow(CarteiraNaoEncontrada::new);
    }


    public CarteirasTransferencia bloquear(TransferirDinheiro transferencia) {

        log.info("Buscando carteiras para transferencia.");
        List<CarteiraTransferencia> carteiras = carteiraRepository.bloquearCarteiras(transferencia.usuarioOrigemId(),transferencia.usuarioDestinoId());
        CarteiraTransferencia origem = localizar(carteiras, transferencia.usuarioOrigemId());
        CarteiraTransferencia destino = localizar(carteiras, transferencia.usuarioDestinoId());
        log.info("Carteiras de origem e destino recebidas.");
        return new CarteirasTransferencia(origem, destino);
    }

    private CarteiraTransferencia localizar(List<CarteiraTransferencia> carteiras, Long usuarioId) {
        return carteiras.stream()
                .filter(carteira -> carteira.getUsuarioId().equals(usuarioId))
                .findFirst()
                .orElseThrow(CarteiraNaoEncontrada::new);
    }
}
