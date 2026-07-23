package com.inter.java.challenge.business;

import com.inter.java.challenge.configuration.exception.exceptions.UsuarioNaoEncontradoException;
import com.inter.java.challenge.model.PaginaUsuario;
import com.inter.java.challenge.model.Usuario;
import com.inter.java.challenge.repository.UsuarioRepository;
import com.inter.java.challenge.workflows.factory.PaginaUsuarioVaziaFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UsuarioBusiness {

    private final UsuarioRepository usuarioRepository;
    private final PaginaUsuarioVaziaFactory paginaUsuarioFactory;

    public Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.buscarUsuarioPorId(id).orElseThrow(UsuarioNaoEncontradoException::new);
    }

    public PaginaUsuario buscarUsuarios(Integer pagina, Integer quantidadePorPagina) {
        return  usuarioRepository.buscarTodosUsuarios(
                quantidadePorPagina,
                pagina
        ).orElseGet(() ->
                paginaUsuarioFactory.criarRetornoVazio(
                        pagina,
                        quantidadePorPagina
                )
        );
    }
}
