package com.consuhelp.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceções para a API REST.
 * <p>
 * Intercepta erros de validação e exceções genéricas, retornando respostas
 * JSON padronizadas ao cliente JavaFX.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata erros de validação do Bean Validation (@Valid).
     * Retorna 400 Bad Request com detalhes dos campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            erros.put(campo, mensagem);
        });

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("timestamp", Instant.now().toString());
        resposta.put("status", 400);
        resposta.put("erro", "Requisição inválida");
        resposta.put("campos", erros);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
    }

    /**
     * Trata exceções genéricas não capturadas.
     * Retorna 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("timestamp", Instant.now().toString());
        resposta.put("status", 500);
        resposta.put("erro", "Erro interno do servidor");
        resposta.put("mensagem", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}
