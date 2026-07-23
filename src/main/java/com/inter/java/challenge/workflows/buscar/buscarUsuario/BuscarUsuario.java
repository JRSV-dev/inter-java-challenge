package com.inter.java.challenge.workflows.buscar.buscarUsuario;

import com.inter.java.challenge.configuration.exception.exceptions.UsuarioNaoEncontradoException;
import com.inter.java.challenge.model.Usuario;
import com.inter.java.challenge.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BuscarUsuario {

    private final UsuarioRepository usuarioRepository;

    public Usuario buscarPorId(Long usuarioId) {
        return usuarioRepository.buscarUsuarioPorId(usuarioId)
                .orElseThrow(UsuarioNaoEncontradoException::new);
    }


}
