package com.inter.java.challenge.configuration.exception;


import com.inter.java.challenge.utils.MensagensExceptions;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {

    private final String codigo;
    private final Integer status;
    private final MensagensExceptions mensagensExceptions;

    protected ApiException(
            CodigoErro codigo,
            HttpStatus status,
            MensagensExceptions mensagensExceptions
    ) {
        this(codigo, status, mensagensExceptions, null);
    }

    protected ApiException(
            CodigoErro codigo,
            HttpStatus status,
            MensagensExceptions mensagensExceptions,
            Throwable cause
    ) {
        super(mensagensExceptions.getMensagem(), cause);
        this.codigo = codigo.name();
        this.status = status.value();
        this.mensagensExceptions = mensagensExceptions;
    }
}
