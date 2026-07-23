package com.inter.java.challenge.workflows.factory;

import com.inter.java.challenge.api.model.PaginaUsuario;
import com.inter.java.challenge.api.model.UsuarioResponse;
import org.springframework.stereotype.Component;

import static java.util.Collections.emptyList;

@Component
public class PaginaUsuarioVaziaFactory {

    public PaginaUsuario criarRetornoVazio(
            Integer pagina,
            Integer quantidadePorPagina
    ) {
        return new PaginaUsuario()
                .conteudo(emptyList())
                .pagina(pagina)
                .quantidadePagina(quantidadePorPagina)
                .totalElementos(0L)
                .totalPaginas(0);
    }
}