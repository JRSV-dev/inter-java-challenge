package com.inter.java.challenge.workflows.buscar.buscarCotacao;

import com.inter.java.challenge.data.records.CotacaoDolar;

import java.time.LocalDate;

public interface BuscarCotacaoDolar {

    public CotacaoDolar buscar(LocalDate dataReferencia);
}
