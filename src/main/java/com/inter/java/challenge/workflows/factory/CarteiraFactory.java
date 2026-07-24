package com.inter.java.challenge.workflows.factory;


import com.inter.java.challenge.data.model.Carteira;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@Component
public class CarteiraFactory {

    public Carteira criarParaUsuario(Long usuarioId) {

        return Carteira.builder()
                .usuarioId(usuarioId)
                .saldoReais(ZERO)
                .saldoDolares(ZERO)
                .build();
    }
}