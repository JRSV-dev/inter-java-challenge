package com.inter.java.challenge.workflows.validator.validarUsuarioRequest.validacoes;


import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.configuration.exception.exceptions.UsuarioJaExisteEmail;
import com.inter.java.challenge.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class EmailUsuarioUnicoValidador
        implements Validador<UsuarioRequest> {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void validar(UsuarioRequest request) {
        usuarioRepository.buscarUsuarioPorEmail(request.getEmail())
                .ifPresent(response ->{
                    throw new UsuarioJaExisteEmail();
                });
    }
}