package com.inter.java.challenge.data.records;

import com.inter.java.challenge.data.model.CarteiraTransferencia;

public record CarteirasTransferencia(
        CarteiraTransferencia origem,
        CarteiraTransferencia destino
) {
}