package com.inter.java.challenge.data.records;


import java.math.BigDecimal;
import java.util.List;

public record BancoCentralCotacaoResponse(
        List<ItemCotacaoBancoCentral> value
) {

    public record ItemCotacaoBancoCentral(
            BigDecimal cotacaoCompra,
            String dataHoraCotacao
    ) {
    }
}