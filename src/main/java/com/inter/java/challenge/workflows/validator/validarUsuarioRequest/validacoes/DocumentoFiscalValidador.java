package com.inter.java.challenge.workflows.validator.validarUsuarioRequest.validacoes;

import com.inter.java.challenge.api.model.UsuarioRequest;
import com.inter.java.challenge.configuration.exception.exceptions.IdentificadorInvalidoException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.inter.java.challenge.utils.ValoresPadrao.TAMANHO_CNPJ;
import static com.inter.java.challenge.utils.ValoresPadrao.TAMANHO_CPF;

@Component
@Order(1)
public class DocumentoFiscalValidador implements Validador<UsuarioRequest> {

    @Override
    public void validar(UsuarioRequest request) {
        if (!documentoValido(request.getIdentificador())) {
            throw new IdentificadorInvalidoException();
        }
    }

    boolean documentoValido(String documento) {
        return Optional.ofNullable(documento)
                .filter(valor ->
                        valor.chars().allMatch(Character::isDigit)
                )
                .filter(valor -> !possuiTodosOsDigitosIguais(valor))
                .map(valor -> switch (valor.length()) {
                    case TAMANHO_CPF -> cpfValido(valor);
                    case TAMANHO_CNPJ -> cnpjValido(valor);
                    default -> false;
                })
                .orElse(false);
    }

    private boolean cpfValido(String cpf) {
        int primeiroDigito = calcularDigito(cpf, 9, 10);
        int segundoDigito = calcularDigito(cpf, 10, 11);
        return digito(cpf, 9) == primeiroDigito
                && digito(cpf, 10) == segundoDigito;
    }

    private boolean cnpjValido(String cnpj) {
        int primeiroDigito = calcularDigito(cnpj, 12, 5);
        int segundoDigito = calcularDigito(cnpj, 13, 6);
        return digito(cnpj, 12) == primeiroDigito
                && digito(cnpj, 13) == segundoDigito;
    }

    private int calcularDigito(
            String documento,
            int tamanhoBase,
            int pesoInicial
    ) {
        int soma = 0;
        int peso = pesoInicial;

        for (int indice = 0; indice < tamanhoBase; indice++) {
            soma += digito(documento, indice) * peso;
            peso = peso == 2 ? 9 : peso - 1;
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private int digito(String documento, int indice) {
        return Character.digit(documento.charAt(indice), 10);
    }

    private boolean possuiTodosOsDigitosIguais(String documento) {
        return documento.chars().distinct().count() == 1;
    }
}
