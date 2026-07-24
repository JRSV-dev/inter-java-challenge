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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    private static final BigDecimal COTACAO = new BigDecimal("5.4321");

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
                .andExpect(jsonPath("$.valorDolar").value(18.4091))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        // Assert: efeitos persistidos
        assertSaldo(1L, "900.00", "50.0000");
        assertSaldo(2L, "1000.00", "68.4091");
        assertTransferenciaPersistida("REAL", "100.00", "18.4091", "5.4321");
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
                                  "valor": 20.1234
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valorReal").value(109.31))
                .andExpect(jsonPath("$.valorDolar").value(20.1234))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));

        // Assert: efeitos persistidos
        assertSaldo(1L, "1000.00", "29.8766");
        assertSaldo(2L, "1109.31", "50.0000");
        assertTransferenciaPersistida("DOLAR", "109.31", "20.1234", "5.4321");
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

    @Test
    void deveRejeitarMoedaDeOrigemInvalidaNoBanco() {
        assertThatThrownBy(() -> inserirTransferencia("EURO", "CONCLUIDA"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void deveRejeitarStatusInvalidoNoBanco() {
        assertThatThrownBy(() -> inserirTransferencia("REAL", "PENDENTE"))
                .isInstanceOf(DataIntegrityViolationException.class);
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
            String moedaOrigemEsperada,
            String valorRealEsperado,
            String valorDolarEsperado,
            String cotacaoEsperada
    ) {
        assertThat(quantidadeTransferencias()).isOne();
        String moedaOrigem = jdbcTemplate.queryForObject(
                "SELECT MOEDA_ORIGEM FROM TRANSFERENCIAS",
                String.class
        );
        BigDecimal valorReal = jdbcTemplate.queryForObject(
                "SELECT VALOR_REAL FROM TRANSFERENCIAS",
                BigDecimal.class
        );
        BigDecimal valorDolar = jdbcTemplate.queryForObject(
                "SELECT VALOR_DOLAR FROM TRANSFERENCIAS",
                BigDecimal.class
        );
        BigDecimal cotacao = jdbcTemplate.queryForObject(
                "SELECT COTACAO_COMPRA FROM TRANSFERENCIAS",
                BigDecimal.class
        );
        assertThat(moedaOrigem).isEqualTo(moedaOrigemEsperada);
        assertThat(valorReal).isEqualByComparingTo(valorRealEsperado);
        assertThat(valorDolar).isEqualByComparingTo(valorDolarEsperado);
        assertThat(cotacao).isEqualByComparingTo(cotacaoEsperada);
    }

    private void inserirTransferencia(String moedaOrigem, String status) {
        jdbcTemplate.update("""
                INSERT INTO TRANSFERENCIAS (
                    USUARIO_ORIGEM_ID,
                    USUARIO_DESTINO_ID,
                    MOEDA_ORIGEM,
                    VALOR_REAL,
                    VALOR_DOLAR,
                    COTACAO_COMPRA,
                    DATA_COTACAO,
                    DATA_TRANSFERENCIA,
                    STATUS
                )
                VALUES (1, 2, ?, 10.00, 2.0000, 5.0000, CURRENT_DATE, CURRENT_TIMESTAMP, ?)
                """, moedaOrigem, status);
    }

    private int quantidadeTransferencias() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TRANSFERENCIAS",
                Integer.class
        );
    }
}
