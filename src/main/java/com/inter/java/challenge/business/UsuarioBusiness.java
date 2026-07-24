package com.inter.java.challenge.business;

import com.inter.java.challenge.api.model.CarteiraResponse;
import com.inter.java.challenge.api.model.PaginaUsuario;
import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.api.model.UsuarioResponse;
import com.inter.java.challenge.data.model.Carteira;
import com.inter.java.challenge.mapper.CarteiraMapper;
import com.inter.java.challenge.mapper.UsuarioMapper;
import com.inter.java.challenge.data.model.Usuario;
import com.inter.java.challenge.repository.CarteiraRepository;
import com.inter.java.challenge.repository.UsuarioRepository;
import com.inter.java.challenge.workflows.buscar.buscarCarteira.BuscarCarteira;
import com.inter.java.challenge.workflows.buscar.buscarUsuario.BuscarUsuario;
import com.inter.java.challenge.workflows.factory.CarteiraFactory;
import com.inter.java.challenge.workflows.factory.PaginaUsuarioVaziaFactory;
import com.inter.java.challenge.workflows.validator.validarUsuarioRequest.CriacaoUsuarioValidador;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.inter.java.challenge.utils.NormalizadorEmail.normalizar;


@Service
@RequiredArgsConstructor
public class UsuarioBusiness {

    private final UsuarioRepository usuarioRepository;
    private final PaginaUsuarioVaziaFactory paginaUsuarioFactory;
    private final UsuarioMapper usuarioMapper;
    private final CriacaoUsuarioValidador criacaoUsuarioValidador;
    private final PasswordEncoder passwordEncoder;
    private final BuscarUsuario buscarUsuario;
    private final CarteiraFactory carteiraFactory;
    private final CarteiraRepository carteiraRepository;
    private final CarteiraMapper carteiraMapper;
    private final BuscarCarteira buscarCarteira;

    public UsuarioResponse buscarUsuarioPorId(Long usuarioId) {
        Usuario model = buscarUsuario.buscarPorId(usuarioId);
        return usuarioMapper.modelParaResponse(model);
    }

    public PaginaUsuario buscarUsuarios(Integer pagina, Integer quantidadePorPagina) {
        return  usuarioRepository.buscarTodosUsuarios(quantidadePorPagina,pagina)
                .map(usuarioMapper::modelPaginaParaResponse)
                .orElseGet(() -> paginaUsuarioFactory.criarRetornoVazio(pagina, quantidadePorPagina));
    }

    @Transactional
    public UsuarioResponse salvarNovoUsuario(UsuarioRequest usuarioRequest) {
        usuarioRequest.setEmail(normalizar(usuarioRequest.getEmail()));
        criacaoUsuarioValidador.validar(usuarioRequest);
        Usuario model = usuarioMapper.requestParaModel(usuarioRequest);
        model.setSenha(passwordEncoder.encode(usuarioRequest.getSenha()));
        usuarioRepository.salvarNovoUsuario(model);
        Long usuarioId = model.getId();
        Usuario usuarioSalvo = buscarUsuario.buscarPorId(usuarioId);
        Carteira carteira = carteiraFactory.criarParaUsuario(usuarioId);
        carteiraRepository.salvarNovaCarteira(carteira);
        return usuarioMapper.modelParaResponse(usuarioSalvo);
    }

    public CarteiraResponse buscarCarteiraPorUsuarioId(Long usuarioId) {
        Carteira carteira = buscarCarteira.buscarCarteiraPeloUsuarioId(usuarioId);
        return carteiraMapper.modelParaResponse(carteira);
    }
}
