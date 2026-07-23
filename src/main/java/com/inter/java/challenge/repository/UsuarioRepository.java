package com.inter.java.challenge.repository;

import com.inter.java.challenge.api.model.PaginaUsuario;
import com.inter.java.challenge.data.model.Pagina;
import com.inter.java.challenge.data.model.Usuario;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UsuarioRepository {

    Optional<Pagina<Usuario>> buscarTodosUsuarios(@Param("quantidade_pagina") Integer quantidadePorPagina,
                                                  @Param("pagina") Integer pagina);
    Optional<Usuario> buscarUsuarioPorId(@Param("usuarioId") Long usuarioId);
    Optional<Usuario> buscarUsuarioPorIdentificador(@Param("identificador") String identificador);
    Optional<Usuario> buscarUsuarioPorEmail(@Param("email") String email);
    Long salvarNovoUsuario(Usuario usuario);
}
