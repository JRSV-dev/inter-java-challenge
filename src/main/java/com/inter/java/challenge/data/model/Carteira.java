package com.inter.java.challenge.data.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carteira {

    private Long id;
    private Long usuarioId;
    private BigDecimal saldoReais;
    private BigDecimal saldoDolares;
    private OffsetDateTime dataAtualizacao;
}