package com.inter.java.challenge.repository;

import com.inter.java.challenge.model.PaginaUsuario;
import com.inter.java.challenge.model.Usuario;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UsuarioRepository {

    Optional<PaginaUsuario> buscarTodosUsuarios(@Param("quantidade_pagina") Integer quantidadePorPagina,
                                 @Param("pagina") Integer pagina);
}
