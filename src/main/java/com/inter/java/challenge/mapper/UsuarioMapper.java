package com.inter.java.challenge.mapper;


import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.api.model.UsuarioResponse;
import com.inter.java.challenge.model.Usuario;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UsuarioMapper {

    UsuarioResponse modelParaResponse(Usuario usuario);
    @Mapping(target = "id", ignore = true)
    Usuario requestParaModel(UsuarioRequest request);
    List<UsuarioResponse> modelParaResponse(List<Usuario> usuarios);


}