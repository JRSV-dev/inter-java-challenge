package com.inter.java.challenge.workflows.validator;

import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.configuration.exception.exceptions.IdentificadorInvalidoException;
import com.inter.java.challenge.workflows.validator.validarUsuarioRequest.validacoes.DocumentoFiscalValidador;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentoFiscalValidadorTest {

    private final DocumentoFiscalValidador validador =
            new DocumentoFiscalValidador();

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",
            "11144477735",
            "11222333000181",
            "04252011000110"
    })
    void deveAceitarCpfECnpjComDigitosVerificadoresValidos(
            String identificador
    ) {
        UsuarioRequest request = requestComIdentificador(identificador);

        assertThatCode(() -> validador.validar(request))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "52998224724",
            "11111111111",
            "11222333000180",
            "00000000000000",
            "1234567890",
            "123456789012"
    })
    void deveRejeitarCpfECnpjInvalidos(String identificador) {
        UsuarioRequest request = requestComIdentificador(identificador);

        assertThatThrownBy(() -> validador.validar(request))
                .isInstanceOf(IdentificadorInvalidoException.class)
                .hasMessage("CPF ou CNPJ inválido.");
    }

    private UsuarioRequest requestComIdentificador(String identificador) {
        return new UsuarioRequest()
                .nomeCompleto("Usuário Teste")
                .email("usuario@teste.com")
                .identificador(identificador)
                .senha("Senha@123");
    }
}
