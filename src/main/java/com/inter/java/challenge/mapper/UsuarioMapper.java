package com.inter.java.challenge.mapper;


import com.inter.java.challenge.api.model.PaginaUsuario;
import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.api.model.UsuarioResponse;
import com.inter.java.challenge.configuration.exception.exceptions.IdentificadorInvalidoException;
import com.inter.java.challenge.data.enums.TipoUsuario;
import com.inter.java.challenge.data.model.Pagina;
import com.inter.java.challenge.data.model.Usuario;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;

import static com.inter.java.challenge.data.enums.TipoUsuario.PF;
import static com.inter.java.challenge.data.enums.TipoUsuario.PJ;
import static com.inter.java.challenge.utils.ValoresPadrao.TAMANHO_CNPJ;
import static com.inter.java.challenge.utils.ValoresPadrao.TAMANHO_CPF;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UsuarioMapper {


    @Mapping(
            target = "tipoUsuario",
            source = "identificador",
            qualifiedByName = "identificadorParaTipoUsuario"
    )
    UsuarioResponse modelParaResponse(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "dataExclusao", ignore = true)
    @Mapping(
            target = "tipoUsuario",
            source = "identificador",
            qualifiedByName = "identificadorParaTipoUsuario"
    )
    Usuario requestParaModel(UsuarioRequest request);


    List<UsuarioResponse> modelListaParaResponse(List<Usuario> usuarios);
    PaginaUsuario modelPaginaParaResponse(Pagina<Usuario> usuario);

    @Named("identificadorParaTipoUsuario")
    default TipoUsuario identificadorParaTipoUsuario(String identificador) {
        return Optional.ofNullable(identificador)
                .map(String::length)
                .flatMap(tamanho ->
                        switch (tamanho) {
                            case TAMANHO_CPF -> Optional.of(PF);
                            case TAMANHO_CNPJ -> Optional.of(PJ);
                            default -> Optional.empty();
                })
                .orElseThrow(IdentificadorInvalidoException::new);
    }


}
