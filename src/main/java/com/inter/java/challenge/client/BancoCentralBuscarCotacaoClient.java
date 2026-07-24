package com.inter.java.challenge.client;


import com.inter.java.challenge.data.records.BancoCentralCotacaoResponse;
import com.inter.java.challenge.data.records.BancoCentralProperties;
import com.inter.java.challenge.configuration.exception.exceptions.CotacaoIndisponivelException;
import com.inter.java.challenge.data.records.CotacaoDolar;
import com.inter.java.challenge.workflows.buscar.buscarCotacao.BuscarCotacaoDolar;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BancoCentralBuscarCotacaoClient implements BuscarCotacaoDolar {

    private static final DateTimeFormatter FORMATO_DATA_BCB =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final String CAMPOS_RETORNO =
            "cotacaoCompra,dataHoraCotacao";
    private static final String ORDENACAO = "dataHoraCotacao desc";
    private static final Integer LIMITE = 1;
    private static final String FORMATO_RETORNO = "json";

    private final BancoCentralCotacaoFeignClient bancoCentralFeignClient;
    private final BancoCentralProperties properties;

    @Override
    public CotacaoDolar buscar(LocalDate dataReferencia) {
        LocalDate dataInicial = dataReferencia.minusDays(properties.diasRetroativos());

        try {
            BancoCentralCotacaoResponse response =
                    bancoCentralFeignClient.buscarUltimaCotacao(
                            formatarData(dataInicial),
                            formatarData(dataReferencia),
                            CAMPOS_RETORNO,
                            ORDENACAO,
                            LIMITE,
                            FORMATO_RETORNO
                    );
            BancoCentralCotacaoResponse.ItemCotacaoBancoCentral item =
                    obterUltimaCotacao(response);

            return new CotacaoDolar(
                    obterCotacaoCompra(item),
                    extrairDataCotacao(item)
            );
        } catch (CotacaoIndisponivelException exception) {
            throw exception;
        } catch (FeignException | IllegalArgumentException exception) {
            throw new CotacaoIndisponivelException(exception);
        }
    }

    private String formatarData(LocalDate data) {
        return FORMATO_DATA_BCB.format(data);
    }

    private BancoCentralCotacaoResponse.ItemCotacaoBancoCentral obterUltimaCotacao(BancoCentralCotacaoResponse response) {
        return Optional.ofNullable(response)
                .map(BancoCentralCotacaoResponse::value)
                .flatMap(itens -> itens.stream().findFirst())
                .orElseThrow(CotacaoIndisponivelException::new);
    }

    private BigDecimal obterCotacaoCompra(BancoCentralCotacaoResponse.ItemCotacaoBancoCentral item) {
        return Optional.ofNullable(item.cotacaoCompra())
                .orElseThrow(CotacaoIndisponivelException::new);
    }

    private LocalDate extrairDataCotacao(BancoCentralCotacaoResponse.ItemCotacaoBancoCentral item) {
        return Optional.ofNullable(item.dataHoraCotacao())
                .filter(dataHora -> dataHora.length() >= 10)
                .map(dataHora -> dataHora.substring(0, 10))
                .map(LocalDate::parse)
                .orElseThrow(CotacaoIndisponivelException::new);
    }
}
