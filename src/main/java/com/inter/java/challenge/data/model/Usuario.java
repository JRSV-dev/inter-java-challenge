package com.inter.java.challenge.data.model;

import com.inter.java.challenge.data.enums.TipoUsuario;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    private Long id;
    private String email;
    private String nomeCompleto;
    private String identificador;
    private TipoUsuario tipoUsuario;
    private String senha;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataExclusao;
}
