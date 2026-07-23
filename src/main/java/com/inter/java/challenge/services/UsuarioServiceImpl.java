package com.inter.java.challenge.services;



import com.inter.java.challenge.api.UsuarioApi;
import com.inter.java.challenge.business.UsuarioBusiness;
import com.inter.java.challenge.model.PaginaUsuario;
import com.inter.java.challenge.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
@RestController
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioApi {

    private final UsuarioBusiness usuarioBusiness;

    @Override
    public ResponseEntity<PaginaUsuario> buscarUsuarios(Integer pagina, Integer quantidadePorPagina){
        return usuarioBusiness.buscarUsuarios(pagina, quantidadePorPagina);
    }

    @Override
    public ResponseEntity<Usuario> buscarUsuarioPorId(Long id){
        return usuarioBusiness.buscarUsuarioPorId(id);
    }
}
