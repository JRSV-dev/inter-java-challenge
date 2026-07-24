package com.inter.java.challenge.workflows.validator.validarUsuarioRequest;


import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.workflows.validator.validarUsuarioRequest.validacoes.Validador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CriacaoUsuarioValidador {

    private final List<Validador<UsuarioRequest>> validadores;

    public void validar(UsuarioRequest request) {
        validadores.forEach(validador -> validador.validar(request));
    }
}