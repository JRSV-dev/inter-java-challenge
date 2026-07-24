package com.inter.java.challenge.service;

import com.inter.java.challenge.api.model.CarteiraResponse;
import com.inter.java.challenge.api.model.PaginaUsuario;
import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.api.model.UsuarioResponse;
import com.inter.java.challenge.business.UsuarioBusiness;
import com.inter.java.challenge.data.model.Carteira;
import com.inter.java.challenge.data.model.Pagina;
import com.inter.java.challenge.data.model.Usuario;
import com.inter.java.challenge.mapper.CarteiraMapper;
import com.inter.java.challenge.mapper.UsuarioMapper;
import com.inter.java.challenge.repository.CarteiraRepository;
import com.inter.java.challenge.repository.UsuarioRepository;
import com.inter.java.challenge.workflows.buscar.buscarUsuario.BuscarCarteira;
import com.inter.java.challenge.workflows.buscar.buscarUsuario.BuscarUsuario;
import com.inter.java.challenge.workflows.factory.CarteiraFactory;
import com.inter.java.challenge.workflows.factory.PaginaUsuarioVaziaFactory;
import com.inter.java.challenge.workflows.validator.validarUsuarioRequest.CriacaoUsuarioValidador;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioBusinessTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PaginaUsuarioVaziaFactory paginaUsuarioFactory;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private CriacaoUsuarioValidador criacaoUsuarioValidador;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BuscarUsuario buscarUsuario;

    @Mock
    private CarteiraFactory carteiraFactory;

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private CarteiraMapper carteiraMapper;

    @Mock
    private BuscarCarteira buscarCarteira;

    @InjectMocks
    private UsuarioBusiness usuarioBusiness;

    @Nested
    class BuscarUsuarioPorId {

        @Test
        void deveBuscarUsuarioPorIdComSucesso() {
            Long usuarioId = 1L;

            Usuario usuario = criarUsuario(usuarioId);
            UsuarioResponse response = criarUsuarioResponse(usuarioId);

            when(buscarUsuario.buscarPorId(usuarioId))
                    .thenReturn(usuario);

            when(usuarioMapper.modelParaResponse(usuario))
                    .thenReturn(response);

            UsuarioResponse resultado =
                    usuarioBusiness.buscarUsuarioPorId(usuarioId);

            assertThat(resultado).isNotNull();
            assertThat(resultado).isEqualTo(response);
            assertThat(resultado.getId()).isEqualTo(usuarioId);

            verify(buscarUsuario).buscarPorId(usuarioId);
            verify(usuarioMapper).modelParaResponse(usuario);

            verifyNoMoreInteractions(
                    buscarUsuario,
                    usuarioMapper
            );
        }
    }

    @Nested
    class BuscarUsuarios {

        @Test
        void deveRetornarUsuariosPaginados() {
            Integer pagina = 0;
            Integer quantidadePorPagina = 10;

            Usuario usuario = criarUsuario(1L);

            Pagina<Usuario> paginaModel = Pagina.<Usuario>builder()
                    .conteudo(List.of(usuario))
                    .pagina(pagina)
                    .quantidadePagina(quantidadePorPagina)
                    .totalElementos(1L)
                    .totalPaginas(1)
                    .build();

            PaginaUsuario paginaResponse = new PaginaUsuario()
                    .conteudo(List.of(criarUsuarioResponse(1L)))
                    .pagina(pagina)
                    .quantidadePagina(quantidadePorPagina)
                    .totalElementos(1L)
                    .totalPaginas(1);

            when(usuarioRepository.buscarTodosUsuarios(
                    quantidadePorPagina,
                    pagina
            )).thenReturn(Optional.of(paginaModel));

            when(usuarioMapper.modelPaginaParaResponse(paginaModel))
                    .thenReturn(paginaResponse);

            PaginaUsuario resultado =
                    usuarioBusiness.buscarUsuarios(
                            pagina,
                            quantidadePorPagina
                    );

            assertThat(resultado).isEqualTo(paginaResponse);
            assertThat(resultado.getConteudo()).hasSize(1);
            assertThat(resultado.getTotalElementos()).isEqualTo(1L);

            verify(usuarioRepository).buscarTodosUsuarios(
                    quantidadePorPagina,
                    pagina
            );

            verify(usuarioMapper)
                    .modelPaginaParaResponse(paginaModel);

            verifyNoInteractions(paginaUsuarioFactory);
        }

        @Test
        void deveRetornarPaginaVaziaQuandoNaoExistiremUsuarios() {
            Integer pagina = 0;
            Integer quantidadePorPagina = 10;

            PaginaUsuario paginaVazia = new PaginaUsuario()
                    .conteudo(List.of())
                    .pagina(pagina)
                    .quantidadePagina(quantidadePorPagina)
                    .totalElementos(0L)
                    .totalPaginas(0);

            when(usuarioRepository.buscarTodosUsuarios(
                    quantidadePorPagina,
                    pagina
            )).thenReturn(Optional.empty());

            when(paginaUsuarioFactory.criarRetornoVazio(
                    pagina,
                    quantidadePorPagina
            )).thenReturn(paginaVazia);

            PaginaUsuario resultado =
                    usuarioBusiness.buscarUsuarios(
                            pagina,
                            quantidadePorPagina
                    );

            assertThat(resultado).isEqualTo(paginaVazia);
            assertThat(resultado.getConteudo()).isEmpty();
            assertThat(resultado.getTotalElementos()).isZero();

            verify(usuarioRepository).buscarTodosUsuarios(
                    quantidadePorPagina,
                    pagina
            );

            verify(paginaUsuarioFactory).criarRetornoVazio(
                    pagina,
                    quantidadePorPagina
            );

            verifyNoInteractions(usuarioMapper);
        }
    }

    @Nested
    class SalvarNovoUsuario {

        @Test
        void deveSalvarUsuarioECriarCarteiraComSucesso() {
            Long usuarioId = 1L;

            UsuarioRequest request = criarUsuarioRequest();
            Usuario usuarioModel = criarUsuario(null);
            Usuario usuarioSalvo = criarUsuario(usuarioId);

            Carteira carteira = criarCarteira(usuarioId);
            UsuarioResponse response = criarUsuarioResponse(usuarioId);

            when(usuarioMapper.requestParaModel(request))
                    .thenReturn(usuarioModel);

            when(passwordEncoder.encode(request.getSenha()))
                    .thenReturn("senha-criptografada");

            when(usuarioRepository.salvarNovoUsuario(usuarioModel))
                    .thenReturn(usuarioId);

            when(buscarUsuario.buscarPorId(usuarioId))
                    .thenReturn(usuarioSalvo);

            when(carteiraFactory.criarParaUsuario(usuarioId))
                    .thenReturn(carteira);

            when(usuarioMapper.modelParaResponse(usuarioSalvo))
                    .thenReturn(response);

            UsuarioResponse resultado =
                    usuarioBusiness.salvarNovoUsuario(request);

            assertThat(resultado).isEqualTo(response);

            assertThat(usuarioModel.getSenha())
                    .isEqualTo("senha-criptografada");

            InOrder ordem = inOrder(
                    criacaoUsuarioValidador,
                    usuarioMapper,
                    passwordEncoder,
                    usuarioRepository,
                    buscarUsuario,
                    carteiraFactory,
                    carteiraRepository
            );

            ordem.verify(criacaoUsuarioValidador)
                    .validar(request);

            ordem.verify(usuarioMapper)
                    .requestParaModel(request);

            ordem.verify(passwordEncoder)
                    .encode(request.getSenha());

            ordem.verify(usuarioRepository)
                    .salvarNovoUsuario(usuarioModel);

            ordem.verify(buscarUsuario)
                    .buscarPorId(usuarioId);

            ordem.verify(carteiraFactory)
                    .criarParaUsuario(usuarioId);

            ordem.verify(carteiraRepository)
                    .salvarNovaCarteira(carteira);

            verify(usuarioMapper)
                    .modelParaResponse(usuarioSalvo);
        }

        @Test
        void naoDeveSalvarUsuarioQuandoValidacaoFalhar() {
            UsuarioRequest request = criarUsuarioRequest();

            RuntimeException erroValidacao =
                    new RuntimeException("Usuário inválido");

            doThrow(erroValidacao)
                    .when(criacaoUsuarioValidador)
                    .validar(request);

            assertThatThrownBy(() ->
                    usuarioBusiness.salvarNovoUsuario(request)
            )
                    .isSameAs(erroValidacao)
                    .hasMessage("Usuário inválido");

            verify(criacaoUsuarioValidador).validar(request);

            verifyNoInteractions(
                    usuarioRepository,
                    usuarioMapper,
                    passwordEncoder,
                    buscarUsuario,
                    carteiraFactory,
                    carteiraRepository
            );
        }

        @Test
        void devePropagarErroQuandoCriacaoDaCarteiraFalhar() {
            Long usuarioId = 1L;

            UsuarioRequest request = criarUsuarioRequest();
            Usuario usuarioModel = criarUsuario(null);
            Usuario usuarioSalvo = criarUsuario(usuarioId);
            Carteira carteira = criarCarteira(usuarioId);

            when(usuarioMapper.requestParaModel(request))
                    .thenReturn(usuarioModel);

            when(passwordEncoder.encode(request.getSenha()))
                    .thenReturn("senha-criptografada");

            when(usuarioRepository.salvarNovoUsuario(usuarioModel))
                    .thenReturn(usuarioId);

            when(buscarUsuario.buscarPorId(usuarioId))
                    .thenReturn(usuarioSalvo);

            when(carteiraFactory.criarParaUsuario(usuarioId))
                    .thenReturn(carteira);

            doThrow(new RuntimeException("Erro ao criar carteira"))
                    .when(carteiraRepository)
                    .salvarNovaCarteira(carteira);

            assertThatThrownBy(() ->
                    usuarioBusiness.salvarNovoUsuario(request)
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Erro ao criar carteira");

            verify(usuarioRepository)
                    .salvarNovoUsuario(usuarioModel);

            verify(carteiraRepository)
                    .salvarNovaCarteira(carteira);

            verify(usuarioMapper, never())
                    .modelParaResponse(any(Usuario.class));
        }
    }

    @Nested
    class BuscarCarteiraPorUsuarioId {

        @Test
        void deveBuscarCarteiraDoUsuarioComSucesso() {
            Long usuarioId = 1L;

            Carteira carteira = criarCarteira(usuarioId);

            CarteiraResponse response = new CarteiraResponse()
                    .id(10L)
                    .usuarioId(usuarioId)
                    .saldoReais(BigDecimal.ZERO)
                    .saldoDolares(BigDecimal.ZERO);

            when(buscarCarteira.buscarCarteiraPeloUsuarioId(usuarioId))
                    .thenReturn(carteira);

            when(carteiraMapper.modelParaResponse(carteira))
                    .thenReturn(response);

            CarteiraResponse resultado =
                    usuarioBusiness.buscarCarteiraPorUsuarioId(usuarioId);

            assertThat(resultado).isEqualTo(response);
            assertThat(resultado.getUsuarioId()).isEqualTo(usuarioId);
            assertThat(resultado.getSaldoReais())
                    .isEqualByComparingTo(BigDecimal.ZERO);

            verify(buscarCarteira)
                    .buscarCarteiraPeloUsuarioId(usuarioId);

            verify(carteiraMapper)
                    .modelParaResponse(carteira);
        }
    }

    private UsuarioRequest criarUsuarioRequest() {
        return new UsuarioRequest()
                .nomeCompleto("João da Silva")
                .email("joao@email.com")
                .identificador("12345678901")
                .senha("Senha@123");
    }

    private Usuario criarUsuario(Long id) {
        return Usuario.builder()
                .id(id)
                .nomeCompleto("João da Silva")
                .email("joao@email.com")
                .identificador("12345678901")
                .senha("senha-criptografada")
                .build();
    }

    private UsuarioResponse criarUsuarioResponse(Long id) {
        return new UsuarioResponse()
                .id(id)
                .nomeCompleto("João da Silva")
                .email("joao@email.com")
                .identificador("12345678901");
    }

    private Carteira criarCarteira(Long usuarioId) {
        return Carteira.builder()
                .id(10L)
                .usuarioId(usuarioId)
                .saldoReais(BigDecimal.ZERO)
                .saldoDolares(BigDecimal.ZERO)
                .build();
    }
}