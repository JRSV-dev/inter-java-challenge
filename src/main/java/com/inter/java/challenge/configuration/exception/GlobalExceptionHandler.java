package com.inter.java.challenge.configuration.exception;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public final class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Erro de negócio. code={}, method={}, path={}, message={}",
                exception.getCodigo(),
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

        return criarResposta(
                HttpStatus.valueOf(exception.getStatus()),
                exception.getCodigo(),
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBodyValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String mensagem = mensagens(
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(erro -> new CampoInvalido(
                                erro.getField(),
                                mensagemPadrao(erro)
                        ))
        );
        return erroDeEntrada(mensagem, request);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodValidation(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        Stream<CampoInvalido> erros = exception.getParameterValidationResults()
                .stream()
                .flatMap(resultado -> resultado.getResolvableErrors().stream()
                        .map(erro -> new CampoInvalido(
                                resultado.getMethodParameter()
                                        .getParameterName(),
                                mensagemPadrao(erro)
                        )));
        return erroDeEntrada(mensagens(erros), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Stream<CampoInvalido> erros = exception.getConstraintViolations()
                .stream()
                .map(violacao -> new CampoInvalido(
                        violacao.getPropertyPath().toString(),
                        violacao.getMessage()
                ));
        return erroDeEntrada(mensagens(erros), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Corpo de requisição inválido. method={}, path={}",
                request.getMethod(),
                request.getRequestURI()
        );
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "REQUISICAO_INVALIDA",
                "O corpo da requisição está ausente ou malformado.",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Violação de integridade. method={}, path={}",
                request.getMethod(),
                request.getRequestURI()
        );
        return criarResposta(
                HttpStatus.CONFLICT,
                "CONFLITO_DE_DADOS",
                "A operação viola uma restrição de integridade dos dados.",
                request
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.NOT_FOUND,
                "RECURSO_NAO_ENCONTRADO",
                "O recurso solicitado não foi encontrado.",
                request
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METODO_NAO_SUPORTADO",
                "O método HTTP não é suportado para este recurso.",
                request
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "TIPO_DE_CONTEUDO_NAO_SUPORTADO",
                "O tipo de conteúdo informado não é suportado.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error(
                "Erro inesperado. method={}, path={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );
        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERRO_INTERNO",
                "Ocorreu um erro interno inesperado.",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> erroDeEntrada(
            String mensagem,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "DADOS_INVALIDOS",
                mensagem,
                request
        );
    }

    private String mensagens(Stream<CampoInvalido> erros) {
        String mensagem = erros
                .map(erro -> erro.campo() + ": " + erro.mensagem())
                .distinct()
                .sorted()
                .collect(Collectors.joining("; "));
        return mensagem.isBlank()
                ? "Os dados informados são inválidos."
                : mensagem;
    }

    private String mensagemPadrao(MessageSourceResolvable erro) {
        return erro.getDefaultMessage() == null
                ? "valor inválido"
                : erro.getDefaultMessage();
    }

    private ResponseEntity<ApiErrorResponse> criarResposta(
            HttpStatus status,
            String codigo,
            String mensagem,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                codigo,
                mensagem,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(response);
    }

    private record CampoInvalido(String campo, String mensagem) {
    }
}
