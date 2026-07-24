package com.inter.java.challenge.integration;

import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.workflows.buscar.buscarCotacao.BuscarCotacaoDolar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:transferencia-it;DB_CLOSE_DELAY=-1"
})
@AutoConfigureMockMvc
@Transactional
class TransferenciaIntegrationTest {

    private static final BigDecimal COTACAO = new BigDecimal("5.0000");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private BuscarCotacaoDolar buscarCotacaoDolar;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM TRANSFERENCIAS");
        jdbcTemplate.update("""
                UPDATE CARTEIRAS
                   SET SALDO_REAIS = 1000.00,
                       SALDO_DOLARES = 50.0000
                 WHERE USUARIO_ID IN (1, 2)
                """);
        when(buscarCotacaoDolar.buscar(any(LocalDate.class)))
                .thenReturn(new CotacaoDolar(
                        COTACAO,
                        LocalDate.of(2026, 7, 23)
                ));
    }

    @Test
    void deveTransferirDeRealParaDolarEPersistirMovimentacao() throws Exception {
        // Act / Assert: contrato HTTP
        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioOrigemId": 1,
                                  "usuarioDestinoId": 2,
                                  "moedaOrigem": "REAL",
                                  "valor": 100.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioOrigemId").value(1))
                .andExpect(jsonPath("$.usuarioDestinoId").value(2))
                .andExpect(jsonPath("$.valorReal").value(100.00))
                .andExpect(jsonPath("$.valorDolar").value(20.00))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        // Assert: efeitos persistidos
        assertSaldo(1L, "900.00", "50.0000");
        assertSaldo(2L, "1000.00", "70.0000");
        assertTransferenciaPersistida("100.00", "20.00");
    }

    @Test
    void deveTransferirDeDolarParaRealEPersistirMovimentacao() throws Exception {
        // Act / Assert: contrato HTTP
        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioOrigemId": 1,
                                  "usuarioDestinoId": 2,
                                  "moedaOrigem": "DOLAR",
                                  "valor": 20.0000
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorReal").value(100.00))
                .andExpect(jsonPath("$.valorDolar").value(20.00))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        // Assert: efeitos persistidos
        assertSaldo(1L, "1000.00", "30.0000");
        assertSaldo(2L, "1100.00", "50.0000");
        assertTransferenciaPersistida("100.00", "20.00");
    }

    @Test
    void naoDeveAlterarSaldosNemPersistirQuandoSaldoForInsuficiente()
            throws Exception {
        // Arrange
        jdbcTemplate.update("""
                UPDATE CARTEIRAS
                   SET SALDO_REAIS = 50.00
                 WHERE USUARIO_ID = 1
                """);

        // Act / Assert
        mockMvc.perform(post("/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "usuarioOrigemId": 1,
                                  "usuarioDestinoId": 2,
                                  "moedaOrigem": "REAL",
                                  "valor": 100.00
                                }
                                """))
                .andExpect(status().isBadRequest());

        assertSaldo(1L, "50.00", "50.0000");
        assertSaldo(2L, "1000.00", "50.0000");
        assertThat(quantidadeTransferencias()).isZero();
    }

    private void assertSaldo(
            long usuarioId,
            String saldoRealEsperado,
            String saldoDolarEsperado
    ) {
        BigDecimal saldoReal = jdbcTemplate.queryForObject(
                "SELECT SALDO_REAIS FROM CARTEIRAS WHERE USUARIO_ID = ?",
                BigDecimal.class,
                usuarioId
        );
        BigDecimal saldoDolar = jdbcTemplate.queryForObject(
                "SELECT SALDO_DOLARES FROM CARTEIRAS WHERE USUARIO_ID = ?",
                BigDecimal.class,
                usuarioId
        );

        assertThat(saldoReal).isEqualByComparingTo(saldoRealEsperado);
        assertThat(saldoDolar).isEqualByComparingTo(saldoDolarEsperado);
    }

    private void assertTransferenciaPersistida(
            String valorRealEsperado,
            String valorDolarEsperado
    ) {
        assertThat(quantidadeTransferencias()).isOne();
        BigDecimal valorReal = jdbcTemplate.queryForObject(
                "SELECT VALOR_REAL FROM TRANSFERENCIAS",
                BigDecimal.class
        );
        BigDecimal valorDolar = jdbcTemplate.queryForObject(
                "SELECT VALOR_DOLAR FROM TRANSFERENCIAS",
                BigDecimal.class
        );
        assertThat(valorReal).isEqualByComparingTo(valorRealEsperado);
        assertThat(valorDolar).isEqualByComparingTo(valorDolarEsperado);
    }

    private int quantidadeTransferencias() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TRANSFERENCIAS",
                Integer.class
        );
    }
}
