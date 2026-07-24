package com.inter.java.challenge.data.records;

import com.inter.java.challenge.data.enums.MoedaOrigem;

import java.math.BigDecimal;

import static com.inter.java.challenge.data.enums.MoedaOrigem.DOLAR;
import static com.inter.java.challenge.data.enums.MoedaOrigem.REAL;

public record TransferirDinheiro(
        Long usuarioOrigemId,
        Long usuarioDestinoId,
        MoedaOrigem moedaOrigem,
        BigDecimal valor
) {

    public boolean transferenciaRealParaDolar() {
        return moedaOrigem.equals(REAL);
    }

    public boolean transferenciaDolarParaReal() {
        return moedaOrigem.equals(DOLAR);
    }
}
