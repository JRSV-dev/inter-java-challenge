package com.inter.java.challenge.business;

import com.inter.java.challenge.model.PaginaUsuario;
import com.inter.java.challenge.model.Usuario;
import com.inter.java.challenge.repository.UsuarioRepository;
import com.inter.java.challenge.workflows.factory.PaginaUsuarioFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioBusiness {

    private final UsuarioRepository usuarioRepository;
    private final PaginaUsuarioFactory paginaUsuarioFactory;

    public ResponseEntity<Usuario> buscarUsuarioPorId(Long id) {
        return ResponseEntity.ok(null);
    }

    public ResponseEntity<PaginaUsuario> buscarUsuarios(Integer pagina, Integer quantidadePorPagina) {
        return ResponseEntity.ok(
                usuarioRepository.buscarTodosUsuarios(
                        quantidadePorPagina,
                        pagina
                ).orElseGet(() ->
                        paginaUsuarioFactory.criarRetornoVazio(
                                pagina,
                                quantidadePorPagina
                        )
                )
        );
    }
}
