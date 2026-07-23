package com.inter.java.challenge.services;



import com.inter.java.challenge.api.UsuarioApi;
import com.inter.java.challenge.api.model.PaginaUsuario;
import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.api.model.UsuarioResponse;
import com.inter.java.challenge.business.UsuarioBusiness;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
@RestController
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioApi {

    private final UsuarioBusiness usuarioBusiness;

    @Override
    public PaginaUsuario buscarUsuarios(Integer pagina, Integer quantidadePorPagina){
        return usuarioBusiness.buscarUsuarios(pagina, quantidadePorPagina);
    }

    @Override
    public UsuarioResponse buscarUsuarioPorId(Long id){
        return usuarioBusiness.buscarUsuarioPorId(id);
    }

    @Override
    public UsuarioResponse salvarUsuario(UsuarioRequest usuarioRequest){
        return usuarioBusiness.salvarNovoUsuario(usuarioRequest);
    }
}
