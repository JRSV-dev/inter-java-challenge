package com.inter.java.challenge.client;

import com.inter.java.challenge.configuration.exception.exceptions.CotacaoIndisponivelException;
import com.inter.java.challenge.data.records.BancoCentralCotacaoResponse;
import com.inter.java.challenge.data.records.BancoCentralProperties;
import com.inter.java.challenge.data.records.CotacaoDolar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BancoCentralBuscarCotacaoClientTest {

    @Mock
    private BancoCentralCotacaoFeignClient bancoCentralFeignClient;

    private BancoCentralBuscarCotacaoClient client;

    @BeforeEach
    void setUp() {
        client = new BancoCentralBuscarCotacaoClient(
                bancoCentralFeignClient,
                new BancoCentralProperties(
                        "https://banco-central.test",
                        7
                )
        );
    }

    @Test
    void deveUsarCotacaoDaSextaFeiraQuandoReferenciaForSabado() {
        // Arrange
        LocalDate sabado = LocalDate.of(2026, 7, 18);
        when(bancoCentralFeignClient.buscarUltimaCotacao(
                "07-11-2026",
                "07-18-2026",
                "cotacaoCompra,dataHoraCotacao",
                "dataHoraCotacao desc",
                1,
                "json"
        )).thenReturn(responseComCotacaoDeSexta());

        // Act
        CotacaoDolar resultado = client.buscar(sabado);

        // Assert
        assertThat(resultado.cotacaoCompra())
                .isEqualByComparingTo("5.4321");
        assertThat(resultado.dataCotacao())
                .isEqualTo(LocalDate.of(2026, 7, 17));
    }

    @Test
    void deveUsarCotacaoDaSextaFeiraQuandoReferenciaForDomingo() {
        // Arrange
        LocalDate domingo = LocalDate.of(2026, 7, 19);
        when(bancoCentralFeignClient.buscarUltimaCotacao(
                "07-12-2026",
                "07-19-2026",
                "cotacaoCompra,dataHoraCotacao",
                "dataHoraCotacao desc",
                1,
                "json"
        )).thenReturn(responseComCotacaoDeSexta());

        // Act
        CotacaoDolar resultado = client.buscar(domingo);

        // Assert
        assertThat(resultado.dataCotacao())
                .isEqualTo(LocalDate.of(2026, 7, 17));
        verify(bancoCentralFeignClient).buscarUltimaCotacao(
                "07-12-2026",
                "07-19-2026",
                "cotacaoCompra,dataHoraCotacao",
                "dataHoraCotacao desc",
                1,
                "json"
        );
    }

    @Test
    void deveRetornarErroDeServicoQuandoNaoExistirCotacaoNaJanela() {
        // Arrange
        when(bancoCentralFeignClient.buscarUltimaCotacao(
                "07-12-2026",
                "07-19-2026",
                "cotacaoCompra,dataHoraCotacao",
                "dataHoraCotacao desc",
                1,
                "json"
        )).thenReturn(new BancoCentralCotacaoResponse(List.of()));

        // Act / Assert
        assertThatThrownBy(() ->
                client.buscar(LocalDate.of(2026, 7, 19))
        ).isInstanceOf(CotacaoIndisponivelException.class);
    }

    private BancoCentralCotacaoResponse responseComCotacaoDeSexta() {
        BancoCentralCotacaoResponse.ItemCotacaoBancoCentral item =
                new BancoCentralCotacaoResponse.ItemCotacaoBancoCentral(
                        new BigDecimal("5.4321"),
                        "2026-07-17 13:10:00.000"
                );
        return new BancoCentralCotacaoResponse(List.of(item));
    }
}
