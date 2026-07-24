package com.inter.java.challenge.services;

import com.inter.java.challenge.api.TransferenciasApi;
import com.inter.java.challenge.api.model.TransferenciaRequest;
import com.inter.java.challenge.api.model.TransferenciaResponse;
import com.inter.java.challenge.business.TransferenciaBusiness;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequiredArgsConstructor
public class TranferenciaServiceImpl implements TransferenciasApi {

    private final TransferenciaBusiness transferenciaBusiness;

    @Override
    public TransferenciaResponse realizarTransferencia(TransferenciaRequest transferenciaRequest){
        return transferenciaBusiness.transferir(transferenciaRequest);
    }
}
