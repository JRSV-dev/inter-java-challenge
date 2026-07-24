package com.inter.java.challenge.workflows.validator.validarUsuarioRequest.validacoes;

import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.configuration.exception.exceptions.UsuarioJaExisteIdentificador;
import com.inter.java.challenge.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
@RequiredArgsConstructor
public class IdentificadorUsuarioUnicoValidador
        implements Validador<UsuarioRequest> {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void validar(UsuarioRequest request) {
        usuarioRepository.buscarUsuarioPorIdentificador(request.getIdentificador())
                .ifPresent(response ->{
                    throw new UsuarioJaExisteIdentificador();
                });

    }
}
