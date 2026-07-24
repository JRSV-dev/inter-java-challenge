package com.inter.java.challenge.data.enums;

import java.math.BigDecimal;

import static com.inter.java.challenge.utils.ValoresPadrao.VALOR_MAXIMO_TRANSFERECIA_PF;
import static com.inter.java.challenge.utils.ValoresPadrao.VALOR_MAXIMO_TRANSFERECIA_PJ;

public enum TipoUsuario {
    PF,
    PJ;

    public BigDecimal limiteDiario() {
        return switch (this) {
            case PF -> VALOR_MAXIMO_TRANSFERECIA_PF;
            case PJ -> VALOR_MAXIMO_TRANSFERECIA_PJ;
        };
    }
}