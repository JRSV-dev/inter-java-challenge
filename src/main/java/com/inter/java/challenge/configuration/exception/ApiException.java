package com.inter.java.challenge.configuration.exception;


import com.inter.java.challenge.utils.MensagensExceptions;
import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {

    private final String codigo;
    private final Integer status;
    private final MensagensExceptions mensagensExceptions;

    protected ApiException(
            String codigo,
            Integer status,
            MensagensExceptions mensagensExceptions
    ) {
        super(mensagensExceptions.getMensagem());
        this.codigo = codigo;
        this.status = status;
        this.mensagensExceptions = mensagensExceptions;
    }
}
