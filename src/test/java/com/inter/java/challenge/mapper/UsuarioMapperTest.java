package com.inter.java.challenge.mapper;

import com.inter.java.challenge.configuration.exception.exceptions.IdentificadorInvalidoException;
import com.inter.java.challenge.data.enums.TipoUsuario;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioMapperTest {

    private final UsuarioMapper mapper =
            Mappers.getMapper(UsuarioMapper.class);

    @ParameterizedTest
    @CsvSource({
            "52998224725, PF",
            "11222333000181, PJ"
    })
    void deveIdentificarTipoPeloTamanhoDoDocumento(
            String identificador,
            TipoUsuario tipoEsperado
    ) {
        TipoUsuario tipo =
                mapper.identificadorParaTipoUsuario(identificador);

        assertThat(tipo).isEqualTo(tipoEsperado);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"123", "123456789012"})
    void deveRejeitarIdentificadorAusenteOuComTamanhoInvalido(
            String identificador
    ) {
        assertThatThrownBy(() ->
                mapper.identificadorParaTipoUsuario(identificador)
        )
                .isInstanceOf(IdentificadorInvalidoException.class)
                .hasMessage("CPF ou CNPJ inválido.");
    }
}
