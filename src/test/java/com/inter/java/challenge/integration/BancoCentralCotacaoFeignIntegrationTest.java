package com.inter.java.challenge.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.inter.java.challenge.client.BancoCentralBuscarCotacaoClient;
import com.inter.java.challenge.data.records.CotacaoDolar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:cotacao-feign-it;DB_CLOSE_DELAY=-1"
})
@EnableWireMock(
        @ConfigureWireMock(
                name = "banco-central",
                baseUrlProperties = "integracoes.banco-central.base-url"
        )
)
class BancoCentralCotacaoFeignIntegrationTest {

    private static final String CAMINHO_COTACAO =
            "/CotacaoDolarPeriodo(" +
                    "dataInicial='07-11-2026'," +
                    "dataFinalCotacao='07-18-2026')";

    @InjectWireMock("banco-central")
    private WireMockServer wireMock;

    @Autowired
    private BancoCentralBuscarCotacaoClient client;

    @BeforeEach
    void setUp() {
        wireMock.resetAll();
    }

    @Test
    void deveMontarContratoFeignERetornarUltimaCotacaoDisponivel() {
        wireMock.stubFor(get(urlPathEqualTo(CAMINHO_COTACAO))
                .withQueryParam("$select", equalTo("cotacaoCompra,dataHoraCotacao"))
                .withQueryParam("$orderby", equalTo("dataHoraCotacao desc"))
                .withQueryParam("$top", equalTo("1"))
                .withQueryParam("$format", equalTo("json"))
                .willReturn(okJson("""
                        {
                          "value": [
                            {
                              "cotacaoCompra": 5.4321,
                              "dataHoraCotacao": "2026-07-17 13:10:00.000"
                            }
                          ]
                        }
                        """)));

        CotacaoDolar resultado =
                client.buscar(LocalDate.of(2026, 7, 18));

        assertThat(resultado.cotacaoCompra())
                .isEqualByComparingTo("5.4321");
        assertThat(resultado.dataCotacao())
                .isEqualTo(LocalDate.of(2026, 7, 17));

        wireMock.verify(getRequestedFor(urlPathEqualTo(CAMINHO_COTACAO))
                .withQueryParam("$select", equalTo("cotacaoCompra,dataHoraCotacao"))
                .withQueryParam("$orderby", equalTo("dataHoraCotacao desc"))
                .withQueryParam("$top", equalTo("1"))
                .withQueryParam("$format", equalTo("json")));
    }
}
