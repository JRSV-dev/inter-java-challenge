package com.inter.java.challenge.data.model;


import com.inter.java.challenge.data.enums.StatusTransferencia;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Transferencia {

    private Long id;

    private Long usuarioOrigemId;
    private Long usuarioDestinoId;

    private BigDecimal valorReal;
    private BigDecimal valorDolar;
    private BigDecimal cotacaoCompra;

    private LocalDate dataCotacao;
    private LocalDateTime dataTransferencia;

    private StatusTransferencia status;
}