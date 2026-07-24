package com.inter.java.challenge.workflows.buscar.buscarUsuario;

import com.inter.java.challenge.configuration.exception.exceptions.CarteiraNaoEncontrada;
import com.inter.java.challenge.data.model.Carteira;
import com.inter.java.challenge.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarCarteira {

    private final CarteiraRepository carteiraRepository;

    public Carteira buscarCarteiraPeloUsuarioId(Long usuarioId){
        return carteiraRepository.buscarPorUsuarioId(usuarioId).orElseThrow(CarteiraNaoEncontrada::new);
    }
}
