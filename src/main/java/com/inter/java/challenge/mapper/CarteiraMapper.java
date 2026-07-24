package com.inter.java.challenge.mapper;

import com.inter.java.challenge.api.model.CarteiraResponse;
import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.data.model.Carteira;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CarteiraMapper {

    CarteiraResponse modelParaResponse(Carteira carteira);
}
