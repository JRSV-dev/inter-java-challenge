package com.inter.java.challenge.client;


import com.inter.java.challenge.data.records.BancoCentralCotacaoResponse;
import com.inter.java.challenge.data.records.BancoCentralProperties;
import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.workflows.buscar.buscarCotacao.BuscarCotacaoDolar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class BancoCentralBuscarCotacaoClient implements BuscarCotacaoDolar {

    private static final DateTimeFormatter FORMATO_DATA_BCB =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final RestClient bancoCentralRestClient;
    private final BancoCentralProperties properties;

    @Override
    public CotacaoDolar buscar(LocalDate dataReferencia) {
        var dataInicial = dataReferencia.minusDays(
                properties.diasRetroativos()
        );

        var response = bancoCentralRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("""
                            /CotacaoDolarPeriodo(
                                dataInicial=@dataInicial,
                                dataFinalCotacao=@dataFinalCotacao
                            )
                            """.replaceAll("\\s+", ""))
                        .queryParam(
                                "@dataInicial",
                                "'%s'".formatted(
                                        FORMATO_DATA_BCB.format(dataInicial)
                                )
                        )
                        .queryParam(
                                "@dataFinalCotacao",
                                "'%s'".formatted(
                                        FORMATO_DATA_BCB.format(dataReferencia)
                                )
                        )
                        .queryParam(
                                "$select",
                                "cotacaoCompra,dataHoraCotacao"
                        )
                        .queryParam(
                                "$orderby",
                                "dataHoraCotacao desc"
                        )
                        .queryParam("$top", 1)
                        .queryParam("$format", "json")
                        .build())
                .retrieve()
                .body(BancoCentralCotacaoResponse.class);

        var item = response.value().getFirst();

        return new CotacaoDolar(
                item.cotacaoCompra(),
                extrairDataCotacao(item.dataHoraCotacao())
        );
    }

    private LocalDate extrairDataCotacao(String dataHoraCotacao) {


        return LocalDate.parse(dataHoraCotacao.substring(0, 10));
    }
}