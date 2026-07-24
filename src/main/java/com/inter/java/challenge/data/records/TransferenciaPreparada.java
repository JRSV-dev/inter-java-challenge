package com.inter.java.challenge.data.records;

public record TransferenciaPreparada(
        TransferirDinheiro comando,
        CotacaoDolar cotacao,
        ValoresTransferencia valores
) {
}
