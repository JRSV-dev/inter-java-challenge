package com.inter.java.challenge.data.records;

import com.inter.java.challenge.data.enums.StatusTransferencia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record TransferenciaResultado(
        Long id,
        Long usuarioOrigemId,
        Long usuarioDestinoId,
        BigDecimal valorReal,
        BigDecimal valorDolar,
        BigDecimal cotacaoCompra,
        LocalDate dataCotacao,
        LocalDateTime dataTransferencia,
        StatusTransferencia status
) {
}