package com.inter.java.challenge.workflows.transferencia;

import com.inter.java.challenge.data.records.CarteirasTransferencia;
import com.inter.java.challenge.data.records.ContextoTransferencia;
import com.inter.java.challenge.data.records.TransferenciaPreparada;
import com.inter.java.challenge.workflows.buscar.buscarCarteira.BuscarCarteira;
import com.inter.java.challenge.workflows.buscar.buscarUsuario.BuscarUsuario;
import com.inter.java.challenge.workflows.buscar.totalTransferenciaDia.ConsultarTotalTransferidoHoje;
import com.inter.java.challenge.workflows.validator.validarTransferencia.ValidarTransferencia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ValidarContextoTransferencia {

    private final BuscarCarteira buscarCarteira;
    private final ConsultarTotalTransferidoHoje consultarTotalTransferidoHoje;
    private final ValidarTransferencia validarTransferencia;
    private final BuscarUsuario buscarUsuario;

    public void validar(TransferenciaPreparada transferencia) {
        buscarUsuario.buscarPorId(transferencia.comando().usuarioOrigemId());
        buscarUsuario.buscarPorId(transferencia.comando().usuarioDestinoId());
        CarteirasTransferencia carteiras = buscarCarteira.bloquear(transferencia.comando());
        BigDecimal totalTransferidoHoje = consultarTotalTransferidoHoje.consultar(transferencia.comando().usuarioOrigemId());
        ContextoTransferencia contexto = new ContextoTransferencia(transferencia.comando(), transferencia.valores(),
                carteiras.origem(), totalTransferidoHoje);
        validarTransferencia.validar(contexto);
    }
}
