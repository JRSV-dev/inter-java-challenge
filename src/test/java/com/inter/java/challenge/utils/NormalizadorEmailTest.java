package com.inter.java.challenge.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NormalizadorEmailTest {

    @Test
    void deveRemoverEspacosExternosEConverterParaMinusculas() {
        String emailNormalizado =
                NormalizadorEmail.normalizar("  Usuario.Teste@EMAIL.COM  ");

        assertThat(emailNormalizado)
                .isEqualTo("usuario.teste@email.com");
    }

    @Test
    void deveManterEmailNulo() {
        assertThat(NormalizadorEmail.normalizar(null)).isNull();
    }
}
