package com.inter.java.challenge.data.model;


import com.inter.java.challenge.data.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CarteiraTransferencia {

    private Long carteiraId;
    private Long usuarioId;
    private TipoUsuario tipoUsuario;
    private BigDecimal saldoReal;
    private BigDecimal saldoDolar;
}