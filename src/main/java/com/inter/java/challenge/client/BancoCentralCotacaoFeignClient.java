package com.inter.java.challenge.client;

import com.inter.java.challenge.data.records.BancoCentralCotacaoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "banco-central-cotacao",
        url = "${integracoes.banco-central.base-url}"
)
public interface BancoCentralCotacaoFeignClient {

    @GetMapping(
            "/CotacaoDolarPeriodo(" +
                    "dataInicial='{dataInicial}'," +
                    "dataFinalCotacao='{dataFinalCotacao}')"
    )
    BancoCentralCotacaoResponse buscarUltimaCotacao(
            @PathVariable("dataInicial") String dataInicial,
            @PathVariable("dataFinalCotacao") String dataFinalCotacao,
            @RequestParam("$select") String campos,
            @RequestParam("$orderby") String ordenacao,
            @RequestParam("$top") Integer limite,
            @RequestParam("$format") String formato
    );
}
