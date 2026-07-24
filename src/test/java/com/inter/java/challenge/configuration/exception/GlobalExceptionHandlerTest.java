package com.inter.java.challenge.configuration.exception;

import com.inter.java.challenge.configuration.exception.exceptions.CarteiraNaoEncontrada;
import com.inter.java.challenge.configuration.exception.exceptions.CotacaoIndisponivelException;
import com.inter.java.challenge.configuration.exception.exceptions.LimiteDiarioExcedidoException;
import com.inter.java.challenge.configuration.exception.exceptions.SaldoInsuficienteException;
import com.inter.java.challenge.configuration.exception.exceptions.UsuarioJaExisteEmail;
import com.inter.java.challenge.configuration.exception.exceptions.UsuarioJaExisteIdentificador;
import com.inter.java.challenge.configuration.exception.exceptions.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request =
            new MockHttpServletRequest("POST", "/transferencias");

    @ParameterizedTest(name = "{2}")
    @MethodSource("excecoesDeNegocio")
    void deveRetornarCodigoEspecificoParaCadaExcecao(
            ApiException exception,
            int status,
            String codigo,
            String mensagem
    ) {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleApiException(exception, request);

        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(response.getBody())
                .extracting(
                        ApiErrorResponse::status,
                        ApiErrorResponse::codigo,
                        ApiErrorResponse::mensagem,
                        ApiErrorResponse::path
                )
                .containsExactly(
                        status,
                        codigo,
                        mensagem,
                        "/transferencias"
                );
    }

    private static Stream<Arguments> excecoesDeNegocio() {
        return Stream.of(
                arguments(
                        new UsuarioNaoEncontradoException(),
                        404,
                        "USUARIO_NAO_ENCONTRADO",
                        "Usuário não encontrado."
                ),
                arguments(
                        new CarteiraNaoEncontrada(),
                        404,
                        "CARTEIRA_NAO_ENCONTRADA",
                        "Nenhuma carteira foi encontrada para o usuário informado."
                ),
                arguments(
                        new UsuarioJaExisteEmail(),
                        409,
                        "USUARIO_EMAIL_JA_CADASTRADO",
                        "Usuário já cadastrado com este e-mail."
                ),
                arguments(
                        new UsuarioJaExisteIdentificador(),
                        409,
                        "USUARIO_IDENTIFICADOR_JA_CADASTRADO",
                        "Usuário já cadastrado com este identificador."
                ),
                arguments(
                        new SaldoInsuficienteException(),
                        422,
                        "SALDO_INSUFICIENTE",
                        "Saldo insuficiente para realizar a transferência."
                ),
                arguments(
                        new LimiteDiarioExcedidoException(),
                        422,
                        "LIMITE_DIARIO_EXCEDIDO",
                        "Limite diário de transferência excedido."
                ),
                arguments(
                        new CotacaoIndisponivelException(),
                        503,
                        "COTACAO_INDISPONIVEL",
                        "Nenhuma cotação disponível foi encontrada."
                )
        );
    }

    @Test
    void deveOcultarDetalhesDaViolacaoDeIntegridade() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleDataIntegrity(
                        new DataIntegrityViolationException(
                                "constraint secreta: usuario_email_key"
                        ),
                        request
                );

        assertThat(response.getStatusCode().value()).isEqualTo(409);
        assertThat(response.getBody().mensagem())
                .isEqualTo(
                        "A operação viola uma restrição de integridade dos dados."
                )
                .doesNotContain("usuario_email_key");
    }

    @Test
    void deveOcultarDetalhesDeErroInesperado() {
        ResponseEntity<ApiErrorResponse> response =
                handler.handleUnexpectedException(
                        new IllegalStateException("senha-interna"),
                        request
                );

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().codigo()).isEqualTo("ERRO_INTERNO");
        assertThat(response.getBody().mensagem())
                .isEqualTo("Ocorreu um erro interno inesperado.")
                .doesNotContain("senha-interna");
    }
}
