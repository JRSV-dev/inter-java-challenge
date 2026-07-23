package com.inter.java.challenge.business;

import com.inter.java.challenge.api.model.PaginaUsuario;
import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.api.model.UsuarioResponse;
import com.inter.java.challenge.configuration.exception.exceptions.UsuarioNaoEncontradoException;
import com.inter.java.challenge.mapper.UsuarioMapper;
import com.inter.java.challenge.model.Usuario;
import com.inter.java.challenge.repository.UsuarioRepository;
import com.inter.java.challenge.workflows.buscar.buscarUsuario.BuscarUsuario;
import com.inter.java.challenge.workflows.factory.PaginaUsuarioVaziaFactory;
import com.inter.java.challenge.workflows.validator.validarUsuarioRequest.CriacaoUsuarioValidador;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UsuarioBusiness {

    private final UsuarioRepository usuarioRepository;
    private final PaginaUsuarioVaziaFactory paginaUsuarioFactory;
    private final UsuarioMapper usuarioMapper;
    private final CriacaoUsuarioValidador criacaoUsuarioValidador;
    private final PasswordEncoder passwordEncoder;
    private final BuscarUsuario buscarUsuario;

    public UsuarioResponse buscarUsuarioPorId(Long usuarioId) {
        Usuario model = buscarUsuario.buscarPorId(usuarioId);
        return usuarioMapper.modelParaResponse(model);
    }

    public PaginaUsuario buscarUsuarios(Integer pagina, Integer quantidadePorPagina) {
        return  usuarioRepository.buscarTodosUsuarios(
                quantidadePorPagina,
                pagina
        ).orElseGet(() ->
                paginaUsuarioFactory.criarRetornoVazio(
                        pagina,
                        quantidadePorPagina
                )
        );
    }

    @Transactional
    public UsuarioResponse salvarNovoUsuario(UsuarioRequest usuarioRequest) {
        criacaoUsuarioValidador.validar(usuarioRequest);
        Usuario model = usuarioMapper.requestParaModel(usuarioRequest);
        model.setSenha(passwordEncoder.encode(usuarioRequest.getSenha()));
        Long usuarioId = usuarioRepository.salvarNovoUsuario(model);
        Usuario usuarioSalvo = buscarUsuario.buscarPorId(usuarioId);
        return usuarioMapper.modelParaResponse(usuarioSalvo);
    }
}
