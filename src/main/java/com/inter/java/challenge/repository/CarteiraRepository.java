package com.inter.java.challenge.repository;


import com.inter.java.challenge.data.model.Carteira;
import com.inter.java.challenge.data.model.CarteiraTransferencia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CarteiraRepository {

    void salvarNovaCarteira(Carteira carteira);
    Optional<Carteira> buscarPorUsuarioId(Long usuarioId);
    List<CarteiraTransferencia> bloquearCarteiras(@Param("usuarioOrigemId") Long usuarioOrigemId, @Param("usuarioDestinoId") Long usuarioDestinoId);
    int debitarSaldoReal(@Param("usuarioId") Long usuarioId, @Param("valor") BigDecimal valor);
    int debitarSaldoDolar(@Param("usuarioId") Long usuarioId, @Param("valor") BigDecimal valor);
    int creditarSaldoReal(@Param("usuarioId") Long usuarioId, @Param("valorReal") BigDecimal valorReal);
    int creditarSaldoDolar(@Param("usuarioId") Long usuarioId, @Param("valorDolar") BigDecimal valorDolar);
}
