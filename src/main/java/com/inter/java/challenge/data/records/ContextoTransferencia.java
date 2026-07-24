package com.inter.java.challenge.data.records;


import com.inter.java.challenge.data.model.CarteiraTransferencia;

import java.math.BigDecimal;

public record ContextoTransferencia(
        TransferirDinheiro model,
        ValoresTransferencia valores,
        CarteiraTransferencia carteiraOrigem,
        BigDecimal totalTransferidoHoje
) {
}
