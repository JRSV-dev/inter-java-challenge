package com.inter.java.challenge.data.records;

import com.inter.java.challenge.data.enums.MoedaOrigem;

import java.math.BigDecimal;

public record TransferirDinheiro(
        Long usuarioOrigemId,
        Long usuarioDestinoId,
        MoedaOrigem moedaOrigem,
        BigDecimal valor
) {

    public boolean transferenciaRealParaDolar() {
        return moedaOrigem == MoedaOrigem.REAL;
    }

    public boolean transferenciaDolarParaReal() {
        return moedaOrigem == MoedaOrigem.DOLAR;
    }
}
