package com.inter.java.challenge.repository;


import com.inter.java.challenge.data.model.Transferencia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface TransferenciaRepository {

    BigDecimal buscarTotalTransferidoNoPeriodo(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
    void salvarTransferencia(Transferencia transferencia);
}