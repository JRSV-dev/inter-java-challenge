package com.inter.java.challenge.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:usuario-it;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
@Transactional
class UsuarioIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void deveNormalizarEmailAntesDeValidarEPersistir() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeCompleto": "Maria de Souza",
                                  "email": "  Maria.Souza@EMAIL.COM  ",
                                  "identificador": "11144477735",
                                  "senha": "Senha@123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email")
                        .value("maria.souza@email.com"));

        String emailPersistido = jdbcTemplate.queryForObject(
                """
                SELECT email
                  FROM usuarios
                 WHERE identificador = '11144477735'
                """,
                String.class
        );

        assertThat(emailPersistido).isEqualTo("maria.souza@email.com");
    }

    @Test
    void deveRejeitarCpfComDigitoVerificadorInvalido() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeCompleto": "CPF Inválido",
                                  "email": "cpf.invalido@email.com",
                                  "identificador": "52998224724",
                                  "senha": "Senha@123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo")
                        .value("IDENTIFICADOR_INVALIDO"))
                .andExpect(jsonPath("$.mensagem")
                        .value("CPF ou CNPJ inválido."));
    }

    @Test
    void deveDetectarEmailDuplicadoAposNormalizacao() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeCompleto": "Outra Ana",
                                  "email": "  ANA.MARTINS@EXAMPLE.COM ",
                                  "identificador": "11144477735",
                                  "senha": "Senha@123"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo")
                        .value("USUARIO_EMAIL_JA_CADASTRADO"));
    }
}
