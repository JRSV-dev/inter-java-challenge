package com.inter.java.challenge.data.records;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CotacaoDolar(
        BigDecimal cotacaoCompra,
        LocalDate dataCotacao
) {

}