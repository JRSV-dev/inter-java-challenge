package com.inter.java.challenge.data.records;

import java.math.BigDecimal;

public record TransferirDinheiro(
        Long usuarioOrigemId,
        Long usuarioDestinoId,
        BigDecimal valorReal,
        BigDecimal valorDolar
) {

}