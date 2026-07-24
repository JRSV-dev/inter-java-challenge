package com.inter.java.challenge.data.records;


import com.inter.java.challenge.data.model.CarteiraTransferencia;

import java.math.BigDecimal;

public record ContextoTransferencia(
        TransferirDinheiro model,
        CarteiraTransferencia carteiraOrigem,
        BigDecimal totalTransferidoHoje
) {
}