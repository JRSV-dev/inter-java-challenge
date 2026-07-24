package com.inter.java.challenge.mapper;

import com.inter.java.challenge.api.model.TransferenciaRequest;
import com.inter.java.challenge.api.model.TransferenciaResponse;
import com.inter.java.challenge.data.records.TransferenciaResultado;
import com.inter.java.challenge.data.records.TransferirDinheiro;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TransferenciaMapper {

    TransferirDinheiro requestParaModel(TransferenciaRequest request);

    TransferenciaResponse resultadoParaResponse(TransferenciaResultado resultado);
}