package com.inter.java.challenge.configuration.exception;


import com.inter.java.challenge.utils.MensagensExceptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class ApiException extends RuntimeException {

    private final String codigo;
    private final Integer status;
    private final MensagensExceptions mensagensExceptions;

}