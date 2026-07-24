package com.inter.java.challenge.repository;


import com.inter.java.challenge.data.model.Carteira;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface CarteiraRepository {

    void salvarNovaCarteira(Carteira carteira);

    Optional<Carteira> buscarPorUsuarioId(Long usuarioId);
}