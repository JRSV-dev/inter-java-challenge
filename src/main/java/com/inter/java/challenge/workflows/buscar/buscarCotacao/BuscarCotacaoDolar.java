package com.inter.java.challenge.workflows.buscar.buscarCotacao;

import com.inter.java.challenge.data.records.CotacaoDolar;

import java.time.LocalDate;

public interface BuscarCotacaoDolar {

    CotacaoDolar buscar(LocalDate dataReferencia);
}
