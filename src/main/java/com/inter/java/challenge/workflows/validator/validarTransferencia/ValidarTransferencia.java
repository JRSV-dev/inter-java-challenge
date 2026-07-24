package com.inter.java.challenge.workflows.validator.validarTransferencia;

import com.inter.java.challenge.data.records.ContextoTransferencia;
import com.inter.java.challenge.workflows.validator.validarTransferencia.validacoes.RegraTransferencia;
import com.inter.java.challenge.workflows.validator.validarUsuarioRequest.validacoes.Validador;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidarTransferencia {

    private final List<RegraTransferencia<ContextoTransferencia>> validadores;

    public void validar(ContextoTransferencia request) {
        log.info("Iniciando as validações das regras de transferência.");
        validadores.forEach(validador -> validador.validar(request));
    }
}
