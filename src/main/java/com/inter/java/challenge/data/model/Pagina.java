package com.inter.java.challenge.data.model;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagina<T> {

    @Builder.Default
    private List<T> conteudo = new ArrayList<>();

    private Integer pagina;

    private Integer quantidadePagina;

    private Long totalElementos;

    private Integer totalPaginas;
}